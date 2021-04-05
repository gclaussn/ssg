package com.github.gclaussn.ssg.server.file;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.file.SiteFileEventListener;

abstract class AbstractFileWatcher implements SiteFileWatcher {

  protected final Logger logger;

  protected final List<SiteFileEventListener> eventListeners;

  protected Site site;

  AbstractFileWatcher() {
    logger = LoggerFactory.getLogger(getClass());
    eventListeners = new LinkedList<>();
  }

  protected abstract void doStart();

  protected abstract void doStop();

  protected abstract boolean isStarted();

  @Override
  public void start(SiteFileEventListener... eventListeners) {
    Objects.requireNonNull(eventListeners, "event listeners are null");

    if (isStarted()) {
      return;
    }

    this.eventListeners.clear();
    this.eventListeners.addAll(Arrays.asList(eventListeners));

    doStart();

    if (isStarted()) {
      logger.info("Started file watcher for site '{}'", site.getPath());
    }
  }

  @Override
  public void stop() {
    if (!isStarted()) {
      return;
    }

    doStop();
    logger.info("Stopped file watcher for site '{}'", site.getPath());
  }
}
