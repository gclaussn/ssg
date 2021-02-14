package com.github.gclaussn.ssg.server.domain;

import java.nio.file.Path;

import com.github.gclaussn.ssg.error.SiteErrorLocation;

public class SiteErrorLocationDTO {

  public static SiteErrorLocationDTO of(SiteErrorLocation location) {
    SiteErrorLocationDTO target = new SiteErrorLocationDTO();
    target.column = location.getColumn();
    target.line = location.getLine();
    target.path = location.getPath();

    return target;
  }

  private int column;
  private int line;
  private Path path;

  public int getColumn() {
    return column;
  }

  public int getLine() {
    return line;
  }

  public Path getPath() {
    return path;
  }
}
