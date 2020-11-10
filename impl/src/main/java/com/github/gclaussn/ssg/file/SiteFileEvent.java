package com.github.gclaussn.ssg.file;

import java.nio.file.Path;

public interface SiteFileEvent {

  SiteFileType getFileType();

  Path getPath();

  long getTimestamp();

  SiteFileEventType getType();
}
