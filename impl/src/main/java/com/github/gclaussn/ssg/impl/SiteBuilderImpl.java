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
import com.github.gclaussn.ssg.plugin.SitePluginGoal;

public class SiteBuilderImpl implements SiteBuilder {

  private SiteConfImpl conf;

  public SiteBuilderImpl() {
    conf = new SiteConfImpl();
  }

  @Override
  public SiteBuilder addEventListener(SiteEventListener eventListener) {
    Objects.requireNonNull(eventListener, "event listener is null");

    conf.eventListeners.add(eventListener);
    return this;
  }

  @Override
  public SiteBuilder addExtension(Object extension) {
    Objects.requireNonNull(extension, "extension is null");

    conf.extensions.add(extension);
    return this;
  }

  @Override
  public SiteBuilder addPageDataSelector(Class<? extends PageDataSelector> pageDataSelectorType) {
    Objects.requireNonNull(pageDataSelectorType, "page data selector type is null");

    conf.pageDataSelectorTypes.add(pageDataSelectorType);
    return this;
  }

  @Override
  public SiteBuilder addPageFilter(Class<? extends PageFilter> pageFilterType) {
    Objects.requireNonNull(pageFilterType, "page filter type is null");

    conf.pageFilterTypes.add(pageFilterType);
    return this;
  }

  @Override
  public SiteBuilder addPageProcessor(Class<? extends PageProcessor> pageProcessorType) {
    Objects.requireNonNull(pageProcessorType, "page processor type is null");

    conf.pageProcessorTypes.add(pageProcessorType);
    return this;
  }

  @Override
  public SiteBuilder addPluginGoal(Class<? extends SitePluginGoal> pluginGoalType) {
    Objects.requireNonNull(pluginGoalType, "plugin goal type is null");

    conf.pluginManager.addPluginGoal(pluginGoalType);
    return this;
  }

  @Override
  public Site build(Path sitePath) {
    Objects.requireNonNull(sitePath, "site path is null");

    // load plugins
    conf.pluginManager.preBuild(this);

    // complete configuration
    SiteConfImpl built = conf.complete();

    Site site = new EventDrivenSiteImpl(sitePath, built);

    // call postBuild hook
    conf.pluginManager.postBuild(site);

    conf = new SiteConfImpl();

    return site;
  }

  @Override
  public SiteBuilder setConsole(SiteConsole console) {
    Objects.requireNonNull(console, "console is null");

    conf.console = console;
    return this;
  }

  @Override
  public SiteBuilder setProperty(String name, Object value) {
    Objects.requireNonNull(name, "name is null");

    conf.properties.put(name, value);
    return this;
  }

  @Override
  public SiteBuilder setPropertyMap(Map<String, Object> properties) {
    Objects.requireNonNull(properties, "properties are null");

    conf.properties.putAll(properties);
    return this;
  }
}
