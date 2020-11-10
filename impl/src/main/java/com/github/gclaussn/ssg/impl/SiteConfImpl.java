package com.github.gclaussn.ssg.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.gclaussn.ssg.PageFilter;
import com.github.gclaussn.ssg.PageProcessor;
import com.github.gclaussn.ssg.conf.SiteConf;
import com.github.gclaussn.ssg.data.PageDataSelector;
import com.github.gclaussn.ssg.event.SiteEventListener;
import com.github.gclaussn.ssg.plugin.SitePlugin;

class SiteConfImpl implements SiteConf {

  protected final List<SitePlugin> plugins;

  protected final Map<String, Object> properties;

  /** Generator extensions. */
  protected final Set<Object> extensions;

  protected List<SiteEventListener> eventListeners;

  protected Set<Class<? extends PageDataSelector>> pageDataSelectorTypes;
  protected Set<Class<? extends PageFilter>> pageFilterTypes;
  protected Set<Class<? extends PageProcessor>> pageProcessorTypes;

  private final SiteConfInjector injector;

  SiteConfImpl() {
    plugins = ServiceLoader.load(SitePlugin.class).stream().map(Provider::get).collect(Collectors.toList());

    properties = new HashMap<>();
    extensions = new HashSet<>();
    
    eventListeners = new LinkedList<>();

    pageDataSelectorTypes = new HashSet<>();
    pageFilterTypes = new HashSet<>();
    pageProcessorTypes = new HashSet<>();

    // initialize injector
    injector = new SiteConfInjector(properties);
  }

  @Override
  public List<SiteEventListener> getEventListeners() {
    return eventListeners;
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
  public Object getProperty(String name) {
    Objects.requireNonNull(name, "name is null");

    return properties.get(name);
  }

  @Override
  public <T> T inject(T instance) {
    return inject(instance, Collections.emptyMap());
  }

  @Override
  public <T> T inject(T instance, Map<String, Object> additionalProperties) {
    return injector.inject(instance, additionalProperties);
  }
}
