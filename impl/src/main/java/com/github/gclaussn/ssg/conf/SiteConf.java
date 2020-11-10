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

  Object getProperty(String name);

  <T> T inject(T instance);

  <T> T inject(T instance, Map<String, Object> additionalProperties);
}
