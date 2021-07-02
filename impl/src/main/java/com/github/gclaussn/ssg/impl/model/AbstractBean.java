package com.github.gclaussn.ssg.impl.model;

import com.github.gclaussn.ssg.PageFilter;
import com.github.gclaussn.ssg.Site;

/**
 * Generic bean.
 *
 * @param <T> The bean type (e.g. {@link PageFilter}).
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
}
