package com.github.gclaussn.ssg.impl;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrLookup;
import org.apache.commons.lang3.text.StrSubstitutor;

import com.github.gclaussn.ssg.conf.SiteProperty;

class SiteConfInjector extends StrLookup<String> {

  private final Map<String, Object> properties;
  private final Map<String, String> env;

  private final StrSubstitutor variableReplacer;

  SiteConfInjector(Map<String, Object> properties) {
    this(properties, System.getenv());
  }

  protected SiteConfInjector(Map<String, Object> properties, Map<String, String> env) {
    this.properties = properties;
    this.env = env;

    variableReplacer = new StrSubstitutor(this);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  protected Object convert(Object value, Class<?> targetType) {
    if (value.getClass() == String.class) {
      // replace environment variables e.g. ${SSG_HOME}
      value = replaceVariables((String) value);
    }

    if (value == null || value.getClass() == targetType) {
      return value;
    }

    if (value.getClass() != String.class) {
      throw new RuntimeException(String.format("Unsupported type for converation: %s", value.getClass()));
    }

    String valueAsString = (String) value;

    if (targetType == Boolean.class) {
      return Boolean.valueOf(valueAsString);
    } else if (targetType == Double.class) {
      return Double.valueOf(valueAsString);
    } else if (targetType == Integer.class) {
      return Integer.valueOf(valueAsString);
    } else if (targetType == Long.class) {
      return Long.valueOf(valueAsString);
    } else if (targetType.isEnum()) {
      return Enum.valueOf((Class<Enum>) targetType, valueAsString);
    } else {
      return valueAsString;
    }
  }

  protected String getEnvironmentVariableName(String name) {
    return name.toUpperCase(Locale.ENGLISH).replace('.', '_');
  }

  protected <T> T inject(T instance, Map<String, Object> additionalProperties) {
    for (Field field : instance.getClass().getDeclaredFields()) {
      inject(instance, additionalProperties, field);
    }

    return instance;
  }

  private void inject(Object instance, Map<String, Object> additionalProperties, Field field) {
    SiteProperty property = field.getAnnotation(SiteProperty.class);
    if (property == null) {
      return;
    }

    String name = property.name();
    if (StringUtils.isBlank(name)) {
      String message = String.format("%s.%s: SiteProperty#name is blank", instance.getClass(), field.getName());
      throw new RuntimeException(message);
    }

    Object value = additionalProperties.get(name);
    if (value == null) {
      // use configuration properties
      value = properties.get(name);
    }

    if (value == null) {
      // use environment variable
      value = System.getenv(getEnvironmentVariableName(name));
    }

    if (value == null) {
      // use default value
      value = property.defaultValue().isEmpty() ? null : property.defaultValue();
    }

    // convert value to the type of the target field
    Object converted = convert(value, field.getType());

    try {
      field.setAccessible(true);
      field.set(instance, converted);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String lookup(String key) {
    if (StringUtils.isBlank(key)) {
      return null;
    } else {
      return env.get(key);
    }
  }

  protected String replaceVariables(String value) {
    return variableReplacer.replace((String) value);
  }
}
