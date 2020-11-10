package com.github.gclaussn.ssg;

import java.util.List;

import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.file.SiteFileEvent;

/**
 * Evicting store for {@link SiteEvent}s and {@link SiteFileEvent}s.
 */
public interface EventStore {

  List<SiteEvent> getEvents();

  List<SiteEvent> getEvents(String sourceId);

  List<SiteEvent> getEvents(String sourceId, long from);

  List<SiteFileEvent> getFileEvents();
}
