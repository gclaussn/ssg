package com.github.gclaussn.ssg.impl;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.PageFilter;
import com.github.gclaussn.ssg.PageFilterBean;
import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.SiteException;
import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.event.SiteEvent;

class PageFilterBeanImpl extends AbstractBean<PageFilter> implements PageFilterBean {

  private SiteImpl site;

  @Override
  public String getId() {
    return id;
  }

  @Override
  protected void init(SiteImpl site, Source source) {
    this.site = site;

    try {
      implementation.init(site);
    } catch (Exception e) {
      throw site.errorFactory.beanNotInitialized(e, id, source).toException();
    }
  }

  @Override
  public boolean filter(Page page) {
    SiteEvent event = site.eventFactory.filterPage(id, page);

    try {
      return implementation.filter(page);
    } catch (Exception e) {
      SiteError error = site.errorFactory.beanExecutionFailed(e, id, page);
      event = event.with(error);
      throw new SiteException(error);
    } finally {
      event.publish(site::publishEvent);
    }
  }

  @Override
  protected void destroy() {
    try {
      implementation.destroy();
    } catch (Exception e) {
      // ignore any exception
    }
  }

  @Override
  public String toString() {
    String implClassName = implementation.getClass().getName();
    return String.format("%s[id=%s,implementation=%]", PageFilterBean.class.getSimpleName(), id, implClassName);
  }
}
