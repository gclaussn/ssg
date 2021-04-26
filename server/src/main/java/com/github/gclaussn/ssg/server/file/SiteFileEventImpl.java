package com.github.gclaussn.ssg.server.file;

import java.nio.file.Path;

import com.github.gclaussn.ssg.file.SiteFileEvent;
import com.github.gclaussn.ssg.file.SiteFileEventType;
import com.github.gclaussn.ssg.file.SiteFileType;

class SiteFileEventImpl implements SiteFileEvent {

  protected SiteFileType fileType;
  protected boolean isPublic;
  protected boolean isSource;
  protected Path path;
  protected long timestamp;
  protected SiteFileEventType type;

  @Override
  public SiteFileType getFileType() {
    return fileType;
  }

  @Override
  public Path getPath() {
    return path;
  }

  @Override
  public long getTimestamp() {
    return timestamp;
  }

  @Override
  public SiteFileEventType getType() {
    return type;
  }

  @Override
  public boolean isSource() {
    return isSource;
  }

  @Override
  public boolean isPublic() {
    return isPublic;
  }
}
