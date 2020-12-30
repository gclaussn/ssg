package com.github.gclaussn.ssg.impl;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.PageSet;
import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.event.SiteEventType;

class SiteEventFactory {

  protected SiteEvent filterPage(String beanId, Page page) {
    SiteEventImpl event = new SiteEventImpl(SiteEventType.FILTER_PAGE);
    event.reference = beanId;
    event.source = page;

    return event;
  }

  protected SiteEvent generatePage(Page page) {
    SiteEventImpl event = new SiteEventImpl(SiteEventType.GENERATE_PAGE);
    event.source = page;

    return event;
  }

  protected SiteEvent generatePageSet(PageSet pageSet) {
    SiteEventImpl event = new SiteEventImpl(SiteEventType.GENERATE_PAGE_SET);
    event.source = pageSet;

    return event;
  }

  protected SiteEvent generateSite() {
    return new SiteEventImpl(SiteEventType.GENERATE_SITE);
  }

  protected SiteEvent loadPage(Source source) {
    SiteEventImpl event = new SiteEventImpl(SiteEventType.LOAD_PAGE);
    event.source = source;
    
    return event;
  }

  protected SiteEvent loadPage(Source source, String pageSetId) {
    SiteEventImpl event = new SiteEventImpl(SiteEventType.LOAD_PAGE);
    event.reference = pageSetId;
    event.source = source;
    
    return event;
  }

  protected SiteEvent loadPageInclude(Source source) {
    SiteEventImpl event = new SiteEventImpl(SiteEventType.LOAD_PAGE_INCLUDE);
    event.source = source;

    return event;
  }

  protected SiteEvent loadPageSet(Source source) {
    SiteEventImpl event = new SiteEventImpl(SiteEventType.LOAD_PAGE_SET);
    event.source = source;

    return event;
  }

  protected SiteEvent loadSite() {
    return new SiteEventImpl(SiteEventType.LOAD_SITE);
  }

  protected SiteEvent processPage(String beanId, Page page) {
    SiteEventImpl event = new SiteEventImpl(SiteEventType.PROCESS_PAGE);
    event.reference = beanId;
    event.source = page;

    return event;
  }

  protected SiteEvent selectData(String beanId, Page page) {
    SiteEventImpl event = new SiteEventImpl(SiteEventType.SELECT_DATA);
    event.reference = beanId;
    event.source = page;

    return event;
  }
}
