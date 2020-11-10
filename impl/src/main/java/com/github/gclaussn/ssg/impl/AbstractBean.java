package com.github.gclaussn.ssg.impl;

import com.github.gclaussn.ssg.PageFilter;
import com.github.gclaussn.ssg.Source;

/**
 * Generic bean.
 *
 * @param <T> The implementation type (e.g. an implementation of {@link PageFilter}).
 */
abstract class AbstractBean<T> {

  protected SiteImpl site;

  protected String id;
  protected T implementation;

  protected abstract void init(SiteImpl site, Source source);

  protected abstract void destroy();
}
