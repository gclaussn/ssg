package com.github.gclaussn.ssg.impl;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

import com.github.gclaussn.ssg.PageFilter;
import com.github.gclaussn.ssg.PageProcessor;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteBuilder;
import com.github.gclaussn.ssg.conf.SiteConsole;
import com.github.gclaussn.ssg.data.PageDataSelector;
import com.github.gclaussn.ssg.event.SiteEventListener;
import com.github.gclaussn.ssg.impl.conf.SiteConfImpl;
import com.github.gclaussn.ssg.impl.event.SiteEventStoreImpl;
import com.github.gclaussn.ssg.impl.plugin.SitePluginManagerImpl;
import com.github.gclaussn.ssg.plugin.SitePluginGoal;

public class SiteBuilderImpl implements SiteBuilder {

  protected SiteConfImpl conf;

  protected SiteEventStoreImpl eventStore;

  protected SitePluginManagerImpl pluginManager;

  public SiteBuilderImpl() {
    conf = new SiteConfImpl();
    eventStore = new SiteEventStoreImpl();
    pluginManager = new SitePluginManagerImpl();
  }

  @Override
  public SiteBuilder addEventListener(SiteEventListener eventListener) {
    Objects.requireNonNull(eventListener, "event listener is null");

    conf.getEventListeners().add(eventListener);
    return this;
  }

  @Override
  public SiteBuilder addExtension(Object extension) {
    Objects.requireNonNull(extension, "extension is null");

    conf.getExtensions().add(extension);
    return this;
  }

  @Override
  public SiteBuilder addPageDataSelector(Class<? extends PageDataSelector> pageDataSelectorType) {
    Objects.requireNonNull(pageDataSelectorType, "page data selector type is null");

    conf.getPageDataSelectorTypes().add(pageDataSelectorType);
    return this;
  }

  @Override
  public SiteBuilder addPageFilter(Class<? extends PageFilter> pageFilterType) {
    Objects.requireNonNull(pageFilterType, "page filter type is null");

    conf.getPageFilterTypes().add(pageFilterType);
    return this;
  }

  @Override
  public SiteBuilder addPageProcessor(Class<? extends PageProcessor> pageProcessorType) {
    Objects.requireNonNull(pageProcessorType, "page processor type is null");

    conf.getPageProcessorTypes().add(pageProcessorType);
    return this;
  }

  @Override
  public SiteBuilder addPluginGoal(Class<? extends SitePluginGoal> pluginGoalType) {
    Objects.requireNonNull(pluginGoalType, "plugin goal type is null");

    pluginManager.addPluginGoal(pluginGoalType);
    return this;
  }

  @Override
  public Site build(Path sitePath) {
    Objects.requireNonNull(sitePath, "site path is null");

    conf.getEventListeners().add(eventStore);

    // load plugins
    pluginManager.preBuild(this);

    Site site = new SiteImpl(this, sitePath);

    // call postBuild hook
    pluginManager.postBuild(site);

    conf = null;
    pluginManager = null;

    return site;
  }

  @Override
  public SiteBuilder setConsole(SiteConsole console) {
    Objects.requireNonNull(console, "console is null");

    conf.setConsole(console);
    return this;
  }

  @Override
  public SiteBuilder setProperty(String name, Object value) {
    Objects.requireNonNull(name, "name is null");

    conf.getProperties().put(name, value);
    return this;
  }

  @Override
  public SiteBuilder setPropertyMap(Map<String, Object> properties) {
    Objects.requireNonNull(properties, "properties are null");

    conf.getProperties().putAll(properties);
    return this;
  }
}
