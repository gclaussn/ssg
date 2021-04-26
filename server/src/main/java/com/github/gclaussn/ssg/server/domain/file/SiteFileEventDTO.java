package com.github.gclaussn.ssg.server.domain.file;

import java.nio.file.Path;

import com.github.gclaussn.ssg.file.SiteFileEvent;
import com.github.gclaussn.ssg.file.SiteFileEventType;
import com.github.gclaussn.ssg.file.SiteFileType;

public class SiteFileEventDTO {

  public static SiteFileEventDTO of(SiteFileEvent event) {
    SiteFileEventDTO target = new SiteFileEventDTO();
    target.fileType = event.getFileType();
    target.isPublic = event.isPublic();
    target.isSource = event.isSource();
    target.path = event.getPath();
    target.timestamp = event.getTimestamp();
    target.type = event.getType();

    return target;
  }

  private SiteFileType fileType;
  private boolean isPublic;
  private boolean isSource;
  private Path path;
  private long timestamp;
  private SiteFileEventType type;

  public SiteFileType getFileType() {
    return fileType;
  }

  public Path getPath() {
    return path;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public SiteFileEventType getType() {
    return type;
  }

  public boolean isPublic() {
    return isPublic;
  }

  public boolean isSource() {
    return isSource;
  }
}
