package com.github.gclaussn.ssg.impl;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.SiteException;
import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.data.PageDataSelector;
import com.github.gclaussn.ssg.data.PageDataSelectorBean;
import com.github.gclaussn.ssg.event.SiteEvent;

class PageDataSelectorBeanImpl extends AbstractBean<PageDataSelector> implements PageDataSelectorBean {

  private SiteImpl site;

  @Override
  public boolean dependsOn(Source source) {
    return implementation.dependsOn(source);
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  protected void init(SiteImpl site, Source source) {
    if (isInitialized()) {
      // if bean is inherited from page set, ensure it is not initialized multiple times
      return;
    }

    this.site = site;

    try {
      implementation.init(site);
    } catch (Exception e) {
      throw site.errorFactory.beanNotInitialized(e, id, source).toException();
    }
  }

  @Override
  public Object select(Page page) {
    SiteEvent event = site.eventFactory.selectData(id, page);

    try {
      return implementation.select(page);
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
    if (isDestroyed()) {
      // if bean is inherited from page set, ensure it is not destroyed multiple times
      return;
    }

    try {
      implementation.destroy();
    } catch (Exception e) {
      // ignore any exception
    } finally {
      // mark bean as destroyed
      site = null;
    }
  }

  protected boolean isInitialized() {
    return site != null;
  }

  protected boolean isDestroyed() {
    return site == null;
  }

  @Override
  public String toString() {
    String implClassName = implementation.getClass().getName();
    return String.format("%s[id=%s,implementation=%]", PageDataSelector.class.getSimpleName(), id, implClassName);
  }
}
