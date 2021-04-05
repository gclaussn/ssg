package com.github.gclaussn.ssg.server.file;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.file.SiteFileEventListener;

/**
 * Recursive file watcher, that informs the listener about created, modified or deleted site
 * files.<br />
 * An instance watches for events that concern the {@code site.yaml} or files within the source path
 * (/src).
 */
public interface SiteFileWatcher {

  static final String TYPE = "ssg.file.watcher";

  /** Name of the file watcher thread. */
  static final String THREAD_NAME = "file";

  /**
   * Creates a new file watcher, watching the sources of the given site.
   * 
   * @param site A specific site.
   * 
   * @return The newly created file watcher that needs to be started.
   * 
   * @see #start(SiteFileEventListener...)
   */
  static SiteFileWatcher of(Site site) {
    return SiteFileWatcherFactory.of(site).create();
  }

  /**
   * Creates a new file watcher of the given type, which watches the sources of the site.
   * 
   * @param site A specific site.
   * 
   * @param type The file watcher type to use.
   * 
   * @return The newly created file watcher that needs to be started.
   */
  static SiteFileWatcher of(Site site, SiteFileWatcherType type) {
    return SiteFileWatcherFactory.of(site).create(type);
  }

  /**
   * Returns the type of the file watcher.
   * 
   * @return The site file watcher type.
   */
  SiteFileWatcherType getType();

  /**
   * Starts the file watcher, using the given event listeners.
   * 
   * @param eventListeners One or more event listeners to use.
   */
  void start(SiteFileEventListener... eventListeners);

  /**
   * Stops the file watcher.
   */
  void stop();
}
