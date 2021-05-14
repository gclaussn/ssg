package com.github.gclaussn.ssg.conf;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.gclaussn.ssg.PageFilter;
import com.github.gclaussn.ssg.PageProcessor;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteBuilder;
import com.github.gclaussn.ssg.data.PageDataSelector;
import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.event.SiteEventListener;
import com.github.gclaussn.ssg.file.SiteFileEvent;
import com.github.gclaussn.ssg.file.SiteFileEventListener;

/**
 * Configuration, used to load and generate the {@link Site}.<br>
 * 
 * @see SiteBuilder
 * @see Site#getConf()
 */
public interface SiteConf extends SiteEventListener, SiteFileEventListener {

  /**
   * Describes the given type and its site properties.
   * 
   * @param type A type.
   * 
   * @return The type description.
   */
  TypeDesc describe(Class<?> type);

  /**
   * Describes the type with the given name and its site properties.
   * 
   * @param typeName The name of a specific type.
   * 
   * @return The type description.
   */
  TypeDesc describe(String typeName);

  /**
   * Gets the site's base path, used if the site is served under a specific path. Default value is
   * {@code /}.
   * 
   * @return The base path.
   */
  String getBasePath();

  /**
   * Provides a console to output messages to {@code stdout} or other print streams.
   * 
   * @return The site's console.
   */
  SiteConsole getConsole();

  /**
   * Provides all registered event listeners.
   * 
   * @return An unmodifiable list, containing the registered event listeners.
   * 
   * @see SiteConfBuilder#addEventListener(SiteEventListener)
   */
  List<SiteEventListener> getEventListeners();

  Set<Object> getExtensions();

  List<SiteFileEventListener> getFileEventListeners();

  Set<Class<? extends PageDataSelector>> getPageDataSelectorTypes();

  Set<Class<? extends PageFilter>> getPageFilterTypes();

  Set<Class<? extends PageProcessor>> getPageProcessorTypes();

  Map<String, Object> getProperties();

  Object getProperty(String propertyName);

  /**
   * Injects {@link SiteProperty} fields of the given instance.
   * 
   * @param <T> The instance type.
   * 
   * @param instance A specific instance, that must not be {@code null}.
   * 
   * @return The instance.
   */
  <T> T inject(T instance);

  <T> T inject(T instance, Map<String, Object> additionalProperties);

  @Override
  void onEvent(SiteEvent event);

  /**
   * 
   */
  @Override
  void onEvent(SiteFileEvent event);
}
