package com.github.gclaussn.ssg.impl.event;

import static com.github.gclaussn.ssg.event.SiteEventType.CREATE_JADE;
import static com.github.gclaussn.ssg.event.SiteEventType.CREATE_YAML;
import static com.github.gclaussn.ssg.event.SiteEventType.DELETE_JADE;
import static com.github.gclaussn.ssg.event.SiteEventType.DELETE_YAML;
import static com.github.gclaussn.ssg.event.SiteEventType.MODIFY_JADE;
import static com.github.gclaussn.ssg.event.SiteEventType.MODIFY_YAML;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.PageSet;
import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.event.SiteEventType;
import com.github.gclaussn.ssg.file.SiteFileEvent;
import com.github.gclaussn.ssg.file.SiteFileType;

public class SiteEventFactory {

  public SiteEvent fileEvent(SiteFileEvent fileEvent, Source source) {
    SiteEventImpl event = new SiteEventImpl();
    event.reference = fileEvent.getPath().toString();
    event.source = source;
    event.timestamp = event.getTimestamp();
    event.type = mapEventType(fileEvent);

    return event;
  }

  public SiteEvent filterPage(String beanId, Page page) {
    SiteEventImpl event = of(SiteEventType.FILTER_PAGE);
    event.reference = beanId;
    event.source = page;

    return event;
  }

  public SiteEvent generatePage(Page page) {
    SiteEventImpl event = of(SiteEventType.GENERATE_PAGE);
    event.source = page;

    return event;
  }

  public SiteEvent generatePageSet(PageSet pageSet) {
    SiteEventImpl event = of(SiteEventType.GENERATE_PAGE_SET);
    event.source = pageSet;

    return event;
  }

  public SiteEvent generateSite() {
    return of(SiteEventType.GENERATE_SITE);
  }

  public SiteEvent loadPage(Source source) {
    SiteEventImpl event = of(SiteEventType.LOAD_PAGE);
    event.source = source;
    
    return event;
  }

  public SiteEvent loadPage(Source source, String pageSetId) {
    SiteEventImpl event = of(SiteEventType.LOAD_PAGE);
    event.reference = pageSetId;
    event.source = source;
    
    return event;
  }

  public SiteEvent loadPageInclude(Source source) {
    SiteEventImpl event = of(SiteEventType.LOAD_PAGE_INCLUDE);
    event.source = source;

    return event;
  }

  public SiteEvent loadPageSet(Source source) {
    SiteEventImpl event = of(SiteEventType.LOAD_PAGE_SET);
    event.source = source;

    return event;
  }

  public SiteEvent loadSite() {
    return of(SiteEventType.LOAD_SITE);
  }

  public SiteEvent processPage(String beanId, Page page) {
    SiteEventImpl event = of(SiteEventType.PROCESS_PAGE);
    event.reference = beanId;
    event.source = page;

    return event;
  }

  public SiteEvent selectData(String beanId, Page page) {
    SiteEventImpl event = of(SiteEventType.SELECT_DATA);
    event.reference = beanId;
    event.source = page;

    return event;
  }

  protected SiteEventType mapEventType(SiteFileEvent fileEvent) {
    switch (fileEvent.getType()) {
      case CREATE:
        return fileEvent.getFileType() == SiteFileType.YAML ? CREATE_YAML : CREATE_JADE;
      case MODIFY:
        return fileEvent.getFileType() == SiteFileType.YAML ? MODIFY_YAML : MODIFY_JADE;
      case DELETE:
        return fileEvent.getFileType() == SiteFileType.YAML ? DELETE_YAML : DELETE_JADE;
      default:
        throw new IllegalArgumentException(String.format("Unsupported file event type %s", fileEvent.getFileType()));
    }
  }

  protected SiteEventImpl of(SiteEventType type) {
    SiteEventImpl event = new SiteEventImpl();
    event.type = type;

    return event;
  }
}
