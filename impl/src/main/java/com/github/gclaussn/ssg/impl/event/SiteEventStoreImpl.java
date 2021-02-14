package com.github.gclaussn.ssg.impl.event;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.SourceType;
import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.event.SiteEventListener;
import com.github.gclaussn.ssg.event.SiteEventStore;
import com.github.gclaussn.ssg.file.SiteFileEvent;
import com.github.gclaussn.ssg.file.SiteFileEventListener;

public class SiteEventStoreImpl implements SiteEventStore, SiteEventListener, SiteFileEventListener {

  private static final int DEFAULT_CAPACITY = 100;
  private static final int DEFAULT_CAPACITY_PER_SOURCE = 10;

  private final EvictingQueue<SiteEvent> events;

  private final Map<String, EvictingQueue<SiteEvent>> eventsBySource;

  private final EvictingQueue<SiteFileEvent> fileEvents;

  public SiteEventStoreImpl() {
    events = new EvictingQueue<>(DEFAULT_CAPACITY);
    eventsBySource = new ConcurrentHashMap<>();

    fileEvents = new EvictingQueue<>(DEFAULT_CAPACITY);
  }

  public void clear() {
    events.clear();

    eventsBySource.values().forEach(EvictingQueue::clear);
    eventsBySource.clear();
  }

  protected EvictingQueue<SiteEvent> createQueue(String sourceId) {
    return new EvictingQueue<>(DEFAULT_CAPACITY_PER_SOURCE);
  }

  @Override
  public List<SiteEvent> getEvents() {
    return events.takeAll();
  }

  @Override
  public List<SiteEvent> getEvents(String sourceId) {
    EvictingQueue<SiteEvent> queue = eventsBySource.get(sourceId);
    return queue != null ? queue.takeAll() : Collections.emptyList();
  }

  @Override
  public List<SiteEvent> getEvents(String sourceId, long from) {
    EvictingQueue<SiteEvent> queue = eventsBySource.get(sourceId);
    return queue != null ? queue.takeWhile(event -> event.getTimestamp() >= from) : Collections.emptyList();
  }

  @Override
  public List<SiteFileEvent> getFileEvents() {
    return fileEvents.takeAll();
  }

  @Override
  public void onEvent(SiteEvent event) {
    events.add(event);

    if (event.getSource().isEmpty()) {
      return;
    }

    Source source = event.getSource().get();
    if (source.getType() != SourceType.SITE) {
      eventsBySource.computeIfAbsent(source.getId(), this::createQueue).add(event);
    }
  }

  @Override
  public void onEvent(SiteFileEvent event) {
    fileEvents.add(event);
  }
}
