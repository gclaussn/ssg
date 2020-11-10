package com.github.gclaussn.ssg;

import java.nio.file.Path;

/**
 * Output file of a static site.
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
