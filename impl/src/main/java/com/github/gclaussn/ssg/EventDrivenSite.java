package com.github.gclaussn.ssg;

import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.file.SiteFileEventListener;

/**
 * A {@link Site} that is driven by file events (create, modify, delete).<br />
 * An event driven site can be used in combination with a {@link SiteFileEventListener} - a watcher
 * (thread) that is watching for file events on the {@code site.yaml} and recursively on the source
 * directory.
 */
public interface EventDrivenSite extends Site, SiteFileEventListener {

  /**
   * Provides the underlying {@link SiteEvent} store.
   * 
   * @return The event store.
   */
  EventStore getEventStore();
}
