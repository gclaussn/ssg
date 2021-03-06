package com.github.gclaussn.ssg.conf;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.gclaussn.ssg.PageFilter;
import com.github.gclaussn.ssg.PageProcessor;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteBuilder;
import com.github.gclaussn.ssg.data.PageDataSelector;
import com.github.gclaussn.ssg.event.SiteEventListener;

/**
 * Configuration, used to load and generate the {@link Site}.<br />
 * 
 * @see SiteBuilder
 * @see Site#getConf()
 */
public interface SiteConf {

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

  Set<Class<? extends PageDataSelector>> getPageDataSelectorTypes();

  Set<Class<? extends PageFilter>> getPageFilterTypes();

  Set<Class<? extends PageProcessor>> getPageProcessorTypes();

  Object getProperty(String propertyName);

  <T> T inject(T instance);

  <T> T inject(T instance, Map<String, Object> additionalProperties);
}
