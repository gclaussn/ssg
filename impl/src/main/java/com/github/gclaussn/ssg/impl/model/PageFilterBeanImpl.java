package com.github.gclaussn.ssg.impl.model;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.PageFilter;
import com.github.gclaussn.ssg.PageFilterBean;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.error.SiteError;
import com.github.gclaussn.ssg.error.SiteException;
import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.event.SiteEventBuilder;
import com.github.gclaussn.ssg.event.SiteEventType;

class PageFilterBeanImpl extends AbstractBean<PageFilter> implements PageFilterBean {

  PageFilterBeanImpl(Site site) {
    super(site);
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  protected void init() {
    try {
      impl.init(site);
    } catch (Exception e) {
      throw SiteError.builder(site).errorBeanNotInitialized(e, id).toException();
    }
  }

  @Override
  public boolean filter(Page page) {
    SiteEventBuilder eventBuilder = SiteEvent.builder().type(SiteEventType.FILTER_PAGE).source(page).reference(id);

    try {
      return impl.filter(page);
    } catch (Exception e) {
      SiteError error = SiteError.builder(site).source(page).errorBeanExecutionFailed(e, id);
      eventBuilder.error(error);
      throw new SiteException(error);
    } finally {
      publish(eventBuilder.build());
    }
  }

  @Override
  protected void destroy() {
    try {
      impl.destroy();
    } catch (Exception e) {
      // ignore any exception
    }
  }

  @Override
  public String toString() {
    String implClassName = impl.getClass().getName();
    return String.format("%s[id=%s,impl=%]", PageFilterBean.class.getSimpleName(), id, implClassName);
  }
}
