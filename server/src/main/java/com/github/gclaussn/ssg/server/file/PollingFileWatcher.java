package com.github.gclaussn.ssg.server.file;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.file.SiteFileEventListener;
import com.github.gclaussn.ssg.file.SiteFileEventType;
import com.github.gclaussn.ssg.file.SiteFileType;

class PollingFileWatcher extends AbstractFileWatcher implements Runnable, ThreadFactory {

  private static final long POLL_PERIOD = 500L;

  private SiteFile siteModel;
  private SiteDirectory sourceDirectory;

  private final Set<SiteFile> changeSet;

  private ScheduledExecutorService executorService;

  private boolean createdOnly;

  PollingFileWatcher() {
    changeSet = new TreeSet<>();
  }

  @Override
  protected void doStart() {
    // initial run
    runInitially();

    try {
      executorService = Executors.newScheduledThreadPool(1, this);
      executorService.scheduleAtFixedRate(this, POLL_PERIOD, POLL_PERIOD, TimeUnit.MILLISECONDS);
    } catch (RuntimeException e) {
      executorService = null;
    }
  }

  @Override
  protected void doStop() {
    if (executorService == null) {
      return;
    }

    try {
      executorService.shutdown();
      executorService.awaitTermination(POLL_PERIOD, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      // ignore exception
    } finally {
      executorService = null;
    }

    sourceDirectory.clear();
  }

  @Override
  public SiteFileWatcherType getType() {
    return SiteFileWatcherType.POLLING;
  }

  protected void handleEvent(SiteFile file) {
    SiteFileEventImpl siteFileEvent = new SiteFileEventImpl();
    siteFileEvent.fileType = SiteFileType.of(file.path);
    siteFileEvent.path = file.path;
    siteFileEvent.timestamp = file.lastModifiedTime;

    if (createdOnly) {
      siteFileEvent.type = SiteFileEventType.CREATE;
    } else if (file.deleted) {
      siteFileEvent.type = SiteFileEventType.DELETE;
    } else {
      siteFileEvent.type = SiteFileEventType.MODIFY;
    }

    for (SiteFileEventListener eventListener : eventListeners) {
      try {
        eventListener.onEvent(siteFileEvent);
      } catch (Exception e) {
        logger.error("Site file event could not be handled", e);
      }
    }
  }

  @Override
  protected boolean isStarted() {
    return executorService != null;
  }

  @Override
  public Thread newThread(Runnable r) {
    return new Thread(r, THREAD_NAME);
  }

  @Override
  public void run() {
    changeSet.clear();

    // poll source directory
    try {
      sourceDirectory.poll(changeSet, createdOnly);
    } catch (IOException e) {
      throw new RuntimeException("Source directory could not be polled", e);
    }

    // poll site model file
    try {
      if (siteModel.poll()) {
        changeSet.add(siteModel);
      }
    } catch (IOException e) {
      throw new RuntimeException("Site model file could not be polled", e);
    }

    // handle change set
    changeSet.forEach(this::handleEvent);

    createdOnly = !createdOnly;
  }

  /**
   * Performans an inital run that fetches source directories and the last modified time of all
   * currently existing source files and the site model file (site.yaml).
   */
  protected void runInitially() {
    siteModel = new SiteFile(site.getPath().resolve(Site.MODEL_NAME));
    sourceDirectory = new SiteDirectory(site.getSourcePath());

    try {
      sourceDirectory.poll(changeSet, true);
    } catch (IOException e) {
      throw new RuntimeException("Source directory could not be polled", e);
    }

    try {
      siteModel.poll();
    } catch (IOException e) {
      throw new RuntimeException("Site model file could not be polled", e);
    }

    createdOnly = true;
  }
}
