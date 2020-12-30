package com.github.gclaussn.ssg.file;

/**
 * Callback interface to be notified of file - e.g. produced by a file watcher.
 */
public interface SiteFileEventListener {

  /**
   * Notifies about a file event that occurred.
   * 
   * @param event A new file event of type {@code CREATE}, {@code MODIFY} or {@code DELETE}.
   */
  void onEvent(SiteFileEvent event);
}
