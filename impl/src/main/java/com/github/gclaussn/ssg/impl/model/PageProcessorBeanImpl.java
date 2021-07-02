package com.github.gclaussn.ssg.impl.model;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.PageProcessor;
import com.github.gclaussn.ssg.PageProcessorBean;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.SiteException;
import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.event.SiteEventBuilder;
import com.github.gclaussn.ssg.event.SiteEventType;

class PageProcessorBeanImpl extends AbstractBean<PageProcessor> implements PageProcessorBean {

  PageProcessorBeanImpl(Site site) {
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
  public Object process(Page page) {
    SiteEventBuilder eventBuilder = SiteEvent.builder().type(SiteEventType.PROCESS_PAGE).source(page).reference(id);

    try {
      return impl.process(page);
    } catch (Exception e) {
      SiteError error = SiteError.builder(site).source(page).errorBeanExecutionFailed(e, id);
      eventBuilder.error(error);
      throw new SiteException(error);
    } finally {
      site.getConfiguration().onEvent(eventBuilder.build());
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
    return String.format("%s[id=%s,impl=%]", PageProcessorBean.class.getSimpleName(), id, implClassName);
  }
}
