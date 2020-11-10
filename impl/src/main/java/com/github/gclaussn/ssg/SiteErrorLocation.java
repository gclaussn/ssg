package com.github.gclaussn.ssg;

import java.nio.file.Path;

/**
 * Location of an error within a source file (YAML or JADE).
 */
public interface SiteErrorLocation {

  int getColumn();

  int getLine();

  Path getPath();

  @Override
  String toString();
}
