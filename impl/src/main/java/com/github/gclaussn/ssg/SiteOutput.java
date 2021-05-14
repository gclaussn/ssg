package com.github.gclaussn.ssg;

import java.nio.file.Path;

/**
 * Output of a static site, which can be a {@link Page}, a public file under {@code pub/} or a
 * {@code node_modules} file.
 */
public interface SiteOutput {

  /**
   * Provides the path to the file on the filesystem.
   * 
   * @return The file path.
   */
  Path getFilePath();

  String getName();

  String getPath();
}
