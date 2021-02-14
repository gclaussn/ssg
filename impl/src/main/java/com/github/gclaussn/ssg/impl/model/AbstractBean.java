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

  protected abstract void init();

  protected abstract void destroy();

  protected void publish(SiteEvent event) {
    site.getConf().getEventListeners().forEach(l -> l.onEvent(event));
  }
}
