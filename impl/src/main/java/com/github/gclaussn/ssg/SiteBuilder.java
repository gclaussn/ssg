package com.github.gclaussn.ssg;

import java.nio.file.Path;
import java.util.Map;

import com.github.gclaussn.ssg.conf.SiteConsole;
import com.github.gclaussn.ssg.data.PageDataSelector;
import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.event.SiteEventListener;
import com.github.gclaussn.ssg.plugin.SitePluginAction;

/**
 * Builder for {@link Site} and {@link EventDrivenSite} instances.
 */
public interface SiteBuilder {

  /**
   * Adds an event listener that is notified when a {@link SiteEvent} occurrs.
   * 
   * @param eventListener An event listener to register.
   * 
   * @return The builder.
   */
  SiteBuilder addEventListener(SiteEventListener eventListener);

  /**
   * Registers a generator extension, that will be accessiable in all JADE templates code via
   * "{@code _ext}" and the classname - e.g. "_ext.myExtension" and "_ext.org.example.myExtension"
   * when the extension object is of type "org.example.MyExtension".
   * 
   * @param extension An extension in form of a bean, providing fields to access and/or methods to
   *        call.
   * 
   * @return The builder.
   */
  SiteBuilder addExtension(Object extension);

  SiteBuilder addPageDataSelector(Class<? extends PageDataSelector> pageDataSelectorType);

  SiteBuilder addPageFilter(Class<? extends PageFilter> pageFilterType);

  SiteBuilder addPageProcessor(Class<? extends PageProcessor> pageProcessorType);

  /**
   * Registers a {@link SitePluginAction}.
   * 
   * @param pluginActionType A plugin action type.
   * 
   * @return The builder.
   */
  SiteBuilder addPluginAction(Class<? extends SitePluginAction> pluginActionType);

  /**
   * Builds the site.
   * 
   * @param sitePath Path to a directory with a {@code site.yaml} file.
   * 
   * @return The newly created {@link Site} instance.
   */
  Site build(Path sitePath);

  /**
   * Sets the console to be used, e.g. by {@link SitePluginAction}s.
   * 
   * @param console A console.
   * 
   * @return The builder.
   */
  SiteBuilder setConsole(SiteConsole console);

  SiteBuilder setProperty(String name, Object value);

  SiteBuilder setPropertyMap(Map<String, Object> properties);
}
