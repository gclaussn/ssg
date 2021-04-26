package com.github.gclaussn.ssg.server.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.file.SiteFileEventListener;
import com.github.gclaussn.ssg.file.SiteFileEventType;
import com.github.gclaussn.ssg.file.SiteFileType;

class SiteFileWatcherImpl implements SiteFileWatcher, Runnable, ThreadFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(SiteFileWatcherImpl.class);

  private static final long POLL_PERIOD = 500L;

  /** The site to overwatch. */
  private final Site site;

  /** The event listener to inform, separated from the {@link #site} field for better testing. */
  private final SiteFileEventListener fileEventListener;

  private final Path siteModelPath;

  private SiteFile siteModel;
  private SiteDirectory sourceDirectory;
  private SiteDirectory publicDirectory;

  private final Set<SiteFile> changeSet;

  private ScheduledExecutorService executorService;

  private boolean createdOnly;

  SiteFileWatcherImpl(Site site) {
    this(site, site);
  }

  protected SiteFileWatcherImpl(Site site, SiteFileEventListener fileEventListener) {
    this.site = site;
    this.fileEventListener = fileEventListener;

    siteModelPath = site.getPath().resolve(Site.MODEL_NAME);

    changeSet = new TreeSet<>();
  }

  protected void handleEvent(SiteFile file) {
    Path path = file.path;

    SiteFileEventImpl siteFileEvent = new SiteFileEventImpl();
    siteFileEvent.fileType = SiteFileType.of(file.path);
    siteFileEvent.path = path;
    siteFileEvent.timestamp = file.lastModifiedTime;

    if (path.startsWith(site.getSourcePath())) {
      siteFileEvent.isSource = true;
    } else if (!path.equals(siteModelPath)) {
      siteFileEvent.isPublic = true;
    }

    if (createdOnly) {
      siteFileEvent.type = SiteFileEventType.CREATE;
    } else if (file.deleted) {
      siteFileEvent.type = SiteFileEventType.DELETE;
    } else {
      siteFileEvent.type = SiteFileEventType.MODIFY;
    }

    try {
      fileEventListener.onEvent(siteFileEvent);
    } catch (Exception e) {
      LOGGER.error("Site file event could not be handled", e);
    }
  }

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
      throw new RuntimeException(String.format("Source directory '%s' could not be polled", Site.SOURCE), e);
    }

    // poll public directory
    try {
      publicDirectory.poll(changeSet, createdOnly);
    } catch (IOException e) {
      throw new RuntimeException(String.format("Public directory '%s' could not be polled", Site.PUBLIC), e);
    }

    // poll site model file
    try {
      if (siteModel.poll()) {
        changeSet.add(siteModel);
      }
    } catch (IOException e) {
      throw new RuntimeException(String.format("File '%s' could not be polled", Site.MODEL_NAME), e);
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
    siteModel = new SiteFile(siteModelPath);
    sourceDirectory = new SiteDirectory(site.getSourcePath(), false);
    publicDirectory = new SiteDirectory(site.getPublicPath(), false);

    try {
      sourceDirectory.poll(changeSet, true);
    } catch (IOException e) {
      throw new RuntimeException(String.format("Source directory '%s' could not be polled", Site.SOURCE), e);
    }

    try {
      publicDirectory.poll(changeSet, true);
    } catch (IOException e) {
      throw new RuntimeException(String.format("Public directory '%s' could not be polled", Site.PUBLIC), e);
    }

    try {
      siteModel.poll();
    } catch (IOException e) {
      throw new RuntimeException(String.format("File '%s' could not be polled", Site.MODEL_NAME), e);
    }

    createdOnly = true;
  }

  @Override
  public void start() {
    if (isStarted()) {
      return;
    }

    // initial run
    runInitially();

    try {
      executorService = Executors.newScheduledThreadPool(1, this);
      executorService.scheduleAtFixedRate(this, POLL_PERIOD, POLL_PERIOD, TimeUnit.MILLISECONDS);
    } catch (RuntimeException e) {
      executorService = null;
    }

    if (isStarted()) {
      LOGGER.info("Started file watcher for site '{}'", site.getPath());
    }
  }

  @Override
  public void stop() {
    if (!isStarted()) {
      return;
    }


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
    publicDirectory.clear();

    LOGGER.info("Stopped file watcher for site '{}'", site.getPath());
  }
}
