package com.github.gclaussn.ssg.server.file;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.file.SiteFileEventListener;
import com.github.gclaussn.ssg.file.SiteFileEventType;
import com.github.gclaussn.ssg.file.SiteFileType;

@SuppressWarnings("unchecked")
class SiteFileWatcherImpl extends AbstractFileWatcher implements Runnable {

  private static final long TIMEOUT = 100L;

  /** Array of event kinds, which should be watched. */
  private static final WatchEvent.Kind<Path>[] EVENTS;

  static {
    EVENTS = (WatchEvent.Kind<Path>[]) new WatchEvent.Kind[3];
    EVENTS[0] = ENTRY_CREATE;
    EVENTS[1] = ENTRY_MODIFY;
    EVENTS[2] = ENTRY_DELETE;
  }

  private final Map<WatchKey, Path> keys;

  /** The watch service instance, if started (see {@link #start()}). Otherwise {@code null}. */
  private WatchService watchService;

  SiteFileWatcherImpl() {
    keys = new HashMap<>();
  }

  @Override
  protected void doStart() {
    Objects.requireNonNull(eventListeners, "event listeners are null");

    try {
      watchService = FileSystems.getDefault().newWatchService();
    } catch (UnsupportedOperationException e) {
      throw new RuntimeException("File watch service is not supported by this file system", e);
    } catch (IOException e) {
      throw new RuntimeException("File watch service could not be created", e);
    }

    try {
      // register directory of site.yaml
      WatchKey key = site.getPath().register(watchService, EVENTS);
      keys.put(key, site.getPath());

      // register source directory recursively
      Files.walkFileTree(site.getSourcePath(), new DirectoryWalker());
    } catch (IOException e) {
      throw new RuntimeException("Directories could not be registered at file watch service", e);
    }

    new Thread(this, THREAD_NAME).start();
  }

  @Override
  protected void doStop() {
    IOUtils.closeQuietly(watchService);
  }

  @Override
  public SiteFileWatcherType getType() {
    return SiteFileWatcherType.WATCHER_SERVICE;
  }

  protected void handleEvent(WatchEvent<?> event, WatchKey key, long timestamp) throws IOException {
    Path context = (Path) event.context();

    // get directory related to watch key
    Path directory = keys.get(key);

    // resolve the path of the current event
    Path path = directory.resolve(context);
    if (directory.equals(site.getPath()) && !context.toString().equals(Site.MODEL_NAME)) {
      // ignore all files beside site.yaml
      return;
    }

    SiteFileEventType fileEventType = mapFileEventType(event);
    if (fileEventType == null) {
      // ignore events with unknown type
      return;
    }

    if (Files.isDirectory(path)) {
      if (fileEventType == SiteFileEventType.CREATE) {
        // register newly created directory and possible sub directories
        Files.walkFileTree(path, new DirectoryWalker());
      }

      // directory events will not be published
      return;
    }

    SiteFileEventImpl siteFileEvent = new SiteFileEventImpl();
    siteFileEvent.fileType = SiteFileType.of(path);
    siteFileEvent.path = path;
    siteFileEvent.timestamp = timestamp;
    siteFileEvent.type = fileEventType;

    for (SiteFileEventListener eventListener : eventListeners) {
      try {
        eventListener.onEvent(siteFileEvent);
      } catch (Exception e) {
        logger.error("Site file event could not be handled", e);
      }
    }
  }

  protected SiteFileEventType mapFileEventType(WatchEvent<?> event) {
    if (event.kind() == ENTRY_CREATE) {
      return SiteFileEventType.CREATE;
    } else if (event.kind() == ENTRY_MODIFY) {
      return SiteFileEventType.MODIFY;
    } else if (event.kind() == ENTRY_DELETE) {
      return SiteFileEventType.DELETE;
    } else {
      return null;
    }
  }

  @Override
  public void run() {
    try {
      WatchKey key;
      while ((key = watchService.take()) != null) {
        // sleep between take() and pollEvents() to eliminate duplicates
        TimeUnit.MILLISECONDS.sleep(TIMEOUT);

        long timestamp = Instant.now().toEpochMilli();
        for (WatchEvent<?> event : key.pollEvents()) {
          handleEvent(event, key, timestamp);
        }

        if (!key.isValid()) {
          key.cancel();

          keys.remove(key);
        } else {
          key.reset();
        }
      }
    } catch (ClosedWatchServiceException e) {
      // ignore exception that occurs when watch service is closed
      return;
    } catch (InterruptedException e) {
      // stop when the thread is interrupted
      return;
    } catch (IOException e) {
      throw new RuntimeException("File watch event could not be processed", e);
    } finally {
      keys.clear();

      IOUtils.closeQuietly(watchService);

      watchService = null;
    }
  }

  @Override
  protected boolean isStarted() {
    return watchService != null;
  }

  private class DirectoryWalker extends SimpleFileVisitor<Path> {

    /**
     * Registers the directory and stores the watch key within a map. The related directory is required
     * for resolving the path of a watch event.
     */
    @Override
    public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attrs) throws IOException {
      WatchKey key = directory.register(watchService, EVENTS);
      keys.put(key, directory);

      return FileVisitResult.CONTINUE;
    }
  }
}
