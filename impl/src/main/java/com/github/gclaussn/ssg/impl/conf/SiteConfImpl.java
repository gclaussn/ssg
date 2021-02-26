package com.github.gclaussn.ssg.impl.conf;

import static com.github.gclaussn.ssg.file.SiteFileType.YAML;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.gclaussn.ssg.PageFilter;
import com.github.gclaussn.ssg.PageProcessor;
import com.github.gclaussn.ssg.conf.SiteConf;
import com.github.gclaussn.ssg.conf.SiteConsole;
import com.github.gclaussn.ssg.conf.SiteProperty;
import com.github.gclaussn.ssg.conf.SitePropertyType;
import com.github.gclaussn.ssg.conf.TypeDesc;
import com.github.gclaussn.ssg.data.PageDataSelector;
import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.event.SiteEventListener;

public class SiteConfImpl implements SiteConf {

  protected final SiteConfInjector injector;

  protected final List<SiteEventListener> eventListeners;
  /** Generator extensions. */
  protected final Set<Object> extensions;
  protected final Set<Class<? extends PageDataSelector>> pageDataSelectorTypes;
  protected final Set<Class<? extends PageFilter>> pageFilterTypes;
  protected final Set<Class<? extends PageProcessor>> pageProcessorTypes;
  protected final Map<String, Object> properties;

  /** YAML object mapper, used to read source models and type descriptions. */
  protected final ObjectMapper objectMapper;

  protected SiteConsole console;

  public SiteConfImpl() {
    injector = new SiteConfInjector(this);

    eventListeners = new LinkedList<>();
    extensions = new HashSet<>();
    pageDataSelectorTypes = new HashSet<>();
    pageFilterTypes = new HashSet<>();
    pageProcessorTypes = new HashSet<>();
    properties = new HashMap<>();

    // configure YAML object mapper
    objectMapper = new ObjectMapper(new YAMLFactory());
    objectMapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
    objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
    objectMapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
    objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

    // default console
    console = new SiteConsoleImpl();
  }

  @Override
  public TypeDesc describe(Class<?> type) {
    TypeDescModel model = readTypeDescModel(type.getName());
    if (model.properties == null) {
      model.properties = Collections.emptyMap();
    }

    TypeDescImpl desc = new TypeDescImpl(model);
    for (Field field : type.getDeclaredFields()) {
      SiteProperty property = field.getAnnotation(SiteProperty.class);
      if (property == null) {
        continue;
      }

      if (StringUtils.isBlank(property.name())) {
        continue;
      }
      if (!property.documented()) {
        continue;
      }

      SitePropertyDescImpl propertyDesc = new SitePropertyDescImpl();
      propertyDesc.defaultValue = property.defaultValue();
      propertyDesc.documentation = model.properties.get(property.name());
      propertyDesc.masked = property.masked();
      propertyDesc.name = property.name();
      propertyDesc.required = property.required();
      propertyDesc.type = SitePropertyType.of(field.getType());
      propertyDesc.typeName = field.getType().getName();

      Object value = properties.get(property.name());
      propertyDesc.value = Objects.toString(value, null);

      desc.addProperty(propertyDesc);
    }

    return desc;
  }

  @Override
  public TypeDesc describe(String typeName) {
    try {
      return describe(Class.forName(typeName));
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(String.format("Type '%s' could not be found", typeName), e);
    }
  }

  @Override
  public SiteConsole getConsole() {
    return console;
  }

  @Override
  public List<SiteEventListener> getEventListeners() {
    return eventListeners;
  }

  @Override
  public Set<Object> getExtensions() {
    return extensions;
  }

  @Override
  public Set<Class<? extends PageDataSelector>> getPageDataSelectorTypes() {
    return pageDataSelectorTypes;
  }

  @Override
  public Set<Class<? extends PageFilter>> getPageFilterTypes() {
    return pageFilterTypes;
  }

  @Override
  public Set<Class<? extends PageProcessor>> getPageProcessorTypes() {
    return pageProcessorTypes;
  }

  @Override
  public Map<String, Object> getProperties() {
    return properties;
  }

  @Override
  public Object getProperty(String propertyName) {
    Objects.requireNonNull(propertyName, "property name is null");

    return properties.get(propertyName);
  }

  @Override
  public <T> T inject(T instance) {
    return inject(instance, Collections.emptyMap());
  }

  @Override
  public <T> T inject(T instance, Map<String, Object> additionalProperties) {
    return injector.inject(instance, additionalProperties);
  }

  @Override
  public void publish(SiteEvent event) {
    eventListeners.forEach(l -> l.onEvent(event));
  }

  protected TypeDescModel readTypeDescModel(String typeName) {
    // e.g. org/example/Plugin.yaml
    String resourceName = YAML.appendTo(typeName.replace('.', '/'));

    InputStream resource = this.getClass().getClassLoader().getResourceAsStream(resourceName);
    if (resource == null) {
      return new TypeDescModel();
    }
    
    try {
      return objectMapper.readValue(resource, TypeDescModel.class);
    } catch (IOException e) {
      throw new RuntimeException(String.format("Type description '%s' could not be read", typeName), e);
    }
  }

  public void setConsole(SiteConsole console) {
    this.console = console;
  }
}
