package com.github.gclaussn.ssg.impl.file;

import static com.github.gclaussn.ssg.file.SiteFileType.JADE;
import static com.github.gclaussn.ssg.file.SiteFileType.YAML;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.file.SiteFileEventListener;
import com.github.gclaussn.ssg.file.SiteFileType;
import com.github.gclaussn.ssg.file.SiteFileWatcher;

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

  protected SiteFileType mapFileType(Path path) {
    if (YAML.isPresent(path)) {
      return SiteFileType.YAML;
    } else if (JADE.isPresent(path)) {
      return SiteFileType.JADE;
    } else {
      return SiteFileType.UNKNOWN;
    }
  }

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
