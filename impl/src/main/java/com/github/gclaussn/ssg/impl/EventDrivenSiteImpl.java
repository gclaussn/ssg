package com.github.gclaussn.ssg.impl;

import static com.github.gclaussn.ssg.event.SiteEventType.CREATE_JADE;
import static com.github.gclaussn.ssg.event.SiteEventType.CREATE_YAML;
import static com.github.gclaussn.ssg.event.SiteEventType.DELETE_JADE;
import static com.github.gclaussn.ssg.event.SiteEventType.DELETE_YAML;
import static com.github.gclaussn.ssg.event.SiteEventType.MODIFY_JADE;
import static com.github.gclaussn.ssg.event.SiteEventType.MODIFY_YAML;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.github.gclaussn.ssg.EventDrivenSite;
import com.github.gclaussn.ssg.EventStore;
import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.PageSet;
import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.SiteException;
import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.SourceType;
import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.event.SiteEventType;
import com.github.gclaussn.ssg.file.SiteFileEvent;
import com.github.gclaussn.ssg.file.SiteFileEventListener;
import com.github.gclaussn.ssg.file.SiteFileEventType;
import com.github.gclaussn.ssg.file.SiteFileType;

class EventDrivenSiteImpl extends SiteImpl implements EventDrivenSite, SiteFileEventListener {

  private final EventStoreImpl eventStore;

  EventDrivenSiteImpl(Path sitePath, SiteConfImpl conf) {
    super(sitePath, conf);

    eventStore = new EventStoreImpl();
  }

  @Override
  protected <V> Map<String, V> createMap(Class<V> valueType) {
    return new ConcurrentHashMap<>();
  }

  protected Source determineSource(String id) {
    Source source = getSource(id);
    if (source.getType() != SourceType.UNKNOWN) {
      return source;
    }

    Optional<String> pageSetId = extractSetId(id);
    if (pageSetId.isPresent()) {
      return new SourceImpl(SourceType.PAGE_SET, pageSetId.get());
    } else if (model.pages.contains(id)) {
      return new SourceImpl(SourceType.PAGE, id);
    } else if (model.pageSets.contains(id)) {
      return new SourceImpl(SourceType.PAGE_SET, id);
    } else if (isPageInclude(id)) {
      return new SourceImpl(SourceType.PAGE_INCLUDE, id);
    } else {
      return new SourceImpl(SourceType.UNKNOWN, id);
    }
  }

  protected Optional<String> extractSetId(String pageId) {
    // use page ID as intial value
    String pageSetId = pageId;

    int index;
    while ((index = pageSetId.lastIndexOf('/')) != -1) {
      pageSetId = pageSetId.substring(0, index);

      if (hasPageSet(pageSetId)) {
        return Optional.of(pageSetId);
      }
    }

    return Optional.empty();
  }

  private boolean filterPage(Page page) {
    return !page.isSkipped() && !page.isRejected();
  }

  @Override
  public EventStore getEventStore() {
    return eventStore;
  }

  /**
   * Checks if the given ID is the ID of a page include that is used within a page.
   * 
   * @param id A specific ID.
   * 
   * @return {@code true}, if the ID identifies a page include. Otherwise {@code false}.
   */
  protected boolean isPageInclude(String id) {
    return pages.values().stream().anyMatch(page -> page.model.includes.contains(id));
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

  protected void modify(Source source) {
    String sourceId = source.getId();

    switch (source.getType()) {
      case PAGE:
        modifyPage(sourceId);
        break;
      case PAGE_INCLUDE:
        modifyPageInclude(sourceId);
        break;
      case PAGE_SET:
        modifyPageSet(sourceId);
        break;
      case SITE:
        load();
        break;
      case UNKNOWN:
        // not possible here
    }
  }

  protected void modifyPage(String pageId) {
    Optional<SiteError> error;
    Optional<PageSet> pageSet;

    // load
    pageSet = hasPage(pageId) ? getPage(pageId).getPageSet() : Optional.empty();
    if (pageSet.isPresent()) {
      error = loadPage(pageId, pageSets.get(pageSet.get().getId()));
    } else {
      error = loadPage(pageId);
    }
    if (error.isPresent()) {
      pages.remove(pageId);
      return;
    }

    Page page = getPage(pageId);

    // generate
    try {
      error = page.generate();
    } catch (SiteException e) {
      // page is skipped or rejected
      return;
    }
    if (error.isPresent()) {
      return;
    }

    pageSet = page.getPageSet();
    if (!pageSet.isPresent()) {
      // no other page is affected
      return;
    }

    String pageSetId = pageSet.get().getId();

    analyzePageSetUsage(pageSetId).stream().filter(this::filterPage).forEach(Page::generate);
  }

  protected void modifyPageInclude(String pageIncludeId) {
    pageIncludes.remove(pageIncludeId);

    List<SiteError> errors = loadPageIncludes(new LinkedList<>(Collections.singletonList(pageIncludeId)));
    if (!errors.isEmpty()) {
      return;
    }

    analyzePageIncludeUsage(pageIncludeId).stream().filter(this::filterPage).forEach(Page::generate);
  }

  protected void modifyPageSet(String pageSetId) {
    // remove page set and related pages
    removePageSet(pageSetId);

    List<SiteError> errors;

    // load
    errors = loadPageSet(pageSetId);
    if (!errors.isEmpty()) {
      return;
    }

    PageSet pageSet = getPageSet(pageSetId);

    // generate
    try {
      errors = pageSet.generate();
    } catch (SiteException e) {
      // page set is skipped
      return;
    }
    if (!errors.isEmpty()) {
      return;
    }

    analyzePageSetUsage(pageSetId).stream().filter(this::filterPage).forEach(Page::generate);
  }

  @Override
  public void onEvent(SiteFileEvent event) {
    switch (event.getFileType()) {
      case YAML:
      case JADE:
        // are supported
        break;
      default:
        // ignore events with file types other than YAML or JADE
        return;
    }

    eventStore.onEvent(event);

    String id = extractId(event.getPath());

    // try to determine, what source (site, page, page include, page set) is affected
    Source source = determineSource(id);

    if (source.getType() == SourceType.UNKNOWN) {
      // ignore unknown source types
      return;
    }

    SiteEventImpl origin = new SiteEventImpl();
    origin.reference = event.getPath().toString();
    origin.source = source;
    origin.timestamp = event.getTimestamp();
    origin.type = mapEventType(event);

    publishEvent(origin);

    if (source.getType() == SourceType.SITE && event.getType() == SiteFileEventType.DELETE) {
      // ignore deletion of site.yaml
      return;
    }

    modify(source);
  }

  /**
   * Publishes the given event to all event listeners and additionally to the event store.
   */
  @Override
  protected void publishEvent(SiteEvent event) {
    super.publishEvent(event);
    eventStore.onEvent(event);
  }

  protected void removePageSet(String pageSetId) {
    if (!hasPageSet(pageSetId)) {
      return;
    }

    PageSetImpl pageSet = pageSets.remove(pageSetId);

    pageSet.pages.forEach(this.pages::remove);
  }

  @Override
  protected void reset() {
    super.reset();

    // clear stored events
    eventStore.clear();
  }
}
