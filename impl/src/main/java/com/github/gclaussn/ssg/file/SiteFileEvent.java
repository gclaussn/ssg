package com.github.gclaussn.ssg.file;

import java.nio.file.Path;

public interface SiteFileEvent {

  SiteFileType getFileType();

  /**
   * Returns the path of the file that has been created, modified or deleted.
   * 
   * @return The file path.
   */
  Path getPath();

  long getTimestamp();

  SiteFileEventType getType();
}
