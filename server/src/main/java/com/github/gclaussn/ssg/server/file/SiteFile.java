package com.github.gclaussn.ssg.server.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class SiteFile implements Comparable<SiteFile> {

  protected final Path path;

  protected boolean deleted = false;
  protected long lastModifiedTime = -1L;

  SiteFile(Path path) {
    this.path = path;
  }

  @Override
  public int compareTo(SiteFile file) {
    int result = Long.compare(lastModifiedTime, file.lastModifiedTime);
    if (result == 0) {
      return path.compareTo(file.path);
    } else {
      return result;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof SiteFile)) {
      return false;
    }

    SiteFile file = (SiteFile) obj;
    return path.equals(file.path);
  }

  @Override
  public int hashCode() {
    return path.hashCode();
  }

  protected boolean poll() throws IOException {
    long lastModifiedTime = Files.getLastModifiedTime(path).toMillis();

    if (this.lastModifiedTime < lastModifiedTime) {
      this.lastModifiedTime = lastModifiedTime;

      return true;
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return path.toString();
  }
}
