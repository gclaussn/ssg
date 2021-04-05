package com.github.gclaussn.ssg.impl.model;

import com.github.gclaussn.ssg.PageFilter;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.event.SiteEvent;

/**
 * Generic bean.
 *
 * @param <T> The implementation type (e.g. an implementation of {@link PageFilter}).
 */
abstract class AbstractBean<T> {

  protected Site site;

  AbstractBean(Site site) {
    this.site = site;
  }

  protected String id;

  /** The underlying implementation. */
  protected T impl;

  /**
   * Initializes the bean, when the site is loaded.
   */
  protected abstract void init();

  /**
   * Destroys the bean, when the site is closed or reloaded.
   */
  protected abstract void destroy();

  protected void publish(SiteEvent event) {
    site.getConfiguration().getEventListeners().forEach(l -> l.onEvent(event));
  }
}
