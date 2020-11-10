package com.github.gclaussn.ssg.impl;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.PageProcessor;
import com.github.gclaussn.ssg.PageProcessorBean;
import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.SiteException;
import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.event.SiteEvent;

class PageProcessorBeanImpl extends AbstractBean<PageProcessor> implements PageProcessorBean {

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
  public Object process(Page page) {
    SiteEvent event = site.eventFactory.processPage(id, page);

    try {
      return implementation.process(page);
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
    return String.format("%s[id=%s,implementation=%]", PageProcessorBean.class.getSimpleName(), id, implClassName);
  }
}
