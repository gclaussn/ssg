package com.github.gclaussn.ssg;

import java.nio.file.Path;
import java.util.Map;

import com.github.gclaussn.ssg.conf.SiteConsole;
import com.github.gclaussn.ssg.data.PageDataSelector;
import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.event.SiteEventListener;
import com.github.gclaussn.ssg.file.SiteFileEventListener;
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

  /**
   * Adds a file event listener that is notified when a file is created, modified or deleted within
   * the site's source ({@code src/} or public ({@code pub/}) directory.
   * 
   * @param fileEventListener A file event listener to register.
   * 
   * @return The builder.
   */
  SiteBuilder addFileEventListener(SiteFileEventListener fileEventListener);

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
   * Sets the site's base path, used if the site is served under a specific path. Default value is
   * {@code /}.
   * 
   * @param basePath The base path, which starts with a slash, but does not end with a slash.
   * 
   * @return The builder.
   */
  SiteBuilder setBasePath(String basePath);

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
