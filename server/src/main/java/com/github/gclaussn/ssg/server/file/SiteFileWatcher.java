package com.github.gclaussn.ssg.server.file;

import com.github.gclaussn.ssg.Site;

/**
 * Recursive file watcher, that informs the listener about created, modified or deleted files.<br />
 * An instance watches for events that concern the {@code site.yaml} or files within the source path
 * (/src).
 */
public interface SiteFileWatcher {

  /** Name of the file watcher thread. */
  static final String THREAD_NAME = "file";

  /**
   * Creates a new file watcher, watching the sources of the given site.
   * 
   * @param site A specific site.
   * 
   * @return The newly created file watcher that needs to be started.
   */
  static SiteFileWatcher of(Site site) {
    return new SiteFileWatcherImpl(site);
  }

  /**
   * Starts the file watcher.
   */
  void start();

  /**
   * Stops the file watcher.
   */
  void stop();
}
