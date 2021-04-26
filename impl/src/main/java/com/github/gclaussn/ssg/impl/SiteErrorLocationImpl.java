package com.github.gclaussn.ssg.impl;

import java.nio.file.Path;

import com.github.gclaussn.ssg.SiteErrorLocation;

class SiteErrorLocationImpl implements SiteErrorLocation {

  protected int column = -1;
  protected int line = -1;
  protected Path path;

  @Override
  public int getColumn() {
    return column;
  }

  @Override
  public int getLine() {
    return line;
  }

  @Override
  public Path getPath() {
    return path;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(path);
    sb.append(" (line: ");
    sb.append(line);

    if (column != -1) {
      sb.append(", column: ");
      sb.append(column);
    }

    sb.append(")");

    return sb.toString();
  }
}
