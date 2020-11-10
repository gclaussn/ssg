package com.github.gclaussn.ssg.file;

import java.nio.file.Path;

public interface SiteFileExtension {

  String appendTo(String value);

  boolean isPresent(Path path);

  boolean isPresent(String value);

  /**
   * Strips the extension from the given value.
   * 
   * @param value A specific value, having the extension.
   * 
   * @return The value without the extension.
   */
  String strip(String value);
}
