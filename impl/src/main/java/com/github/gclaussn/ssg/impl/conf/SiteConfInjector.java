package com.github.gclaussn.ssg.impl.conf;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrLookup;
import org.apache.commons.lang3.text.StrSubstitutor;

import com.github.gclaussn.ssg.conf.SiteConsole;
import com.github.gclaussn.ssg.conf.SiteProperty;
import com.github.gclaussn.ssg.conf.SitePropertyDesc;
import com.github.gclaussn.ssg.conf.SitePropertyType;

class SiteConfInjector extends StrLookup<String> {

  private final SiteConfImpl conf;

  private final Map<String, String> env;

  private final StrSubstitutor variableReplacer;

  SiteConfInjector(SiteConfImpl conf) {
    this(conf, System.getenv());
  }

  protected SiteConfInjector(SiteConfImpl conf, Map<String, String> env) {
    this.conf = conf;
    this.env = env;

    variableReplacer = new StrSubstitutor(this);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  protected Object convert(Object value, Class<?> targetType, SitePropertyDesc desc) {
    if (value == null) {
      return value;
    }

    if (value.getClass() == String.class) {
      // replace environment variables e.g. ${SSG_HOME}
      String s = replaceVariables((String) value);

      switch (desc.getType()) {
        case BOOLEAN:
          return Boolean.valueOf(s);
        case DOUBLE:
          return Double.valueOf(s);
        case ENUM:
          return Enum.valueOf((Class<Enum>) targetType, s);
        case INTEGER:
          return Integer.valueOf(s);
        case LONG:
          return Long.valueOf(s);
        case STRING:
          return s.isEmpty() ? null : s;
        default:
          // will result in an unsupported type conversion
      }
    }

    if (value.getClass() == targetType) {
      return value;
    }

    if (targetType.isAssignableFrom(value.getClass())) {
      return value;
    }

    throw new RuntimeException(String.format("Unsupported type for conversion: %s", value.getClass()));
  }

  protected <T> T inject(T instance) {
    return inject(instance, Collections.emptyMap());
  }

  protected <T> T inject(T instance, Map<String, Object> additionalProperties) {
    for (Field field : instance.getClass().getDeclaredFields()) {
      if (field.getType() == SiteConsole.class) {
        injectConsole(instance, field, additionalProperties);
      } else {
        inject(instance, additionalProperties, field);
      }
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
      String message = String.format("%s#%s: Property name is blank", instance.getClass(), field.getName());
      throw new RuntimeException(message);
    }

    SitePropertyDescImpl desc = new SitePropertyDescImpl();
    desc.name = property.name();
    desc.type = SitePropertyType.of(field.getType());

    Object value = additionalProperties.get(name);
    if (value == null) {
      // use configuration properties
      value = conf.properties.get(name);
    }

    if (value == null) {
      // use environment variable
      value = System.getenv(desc.getVariableName());
    }

    if (value == null) {
      // use default value
      value = property.defaultValue().isEmpty() ? null : property.defaultValue();
    }

    // convert value to the type of the target field
    Object converted = convert(value, field.getType(), desc);

    if (value == null && property.required()) {
      String message = new StringBuilder()
          .append(instance.getClass())
          .append('#')
          .append(field.getName())
          .append(": Property '")
          .append(desc.getName())
          .append("' is required, but no value has been provided")
          .toString();

      throw new RuntimeException(message);
    }

    try {
      field.setAccessible(true);
      field.set(instance, converted);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(e);
    }
  }

  private void injectConsole(Object instance, Field field, Map<String, Object> additionalProperties) {
    Object console = additionalProperties.get(SiteConsole.PROPERTY_NAME);

    try {
      field.setAccessible(true);
      field.set(instance, console != null ? console : conf.getConsole());
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
