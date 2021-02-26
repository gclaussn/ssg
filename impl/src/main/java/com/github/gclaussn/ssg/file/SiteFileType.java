package com.github.gclaussn.ssg.file;

import java.nio.file.Path;
import java.util.Locale;

public enum SiteFileType implements SiteFileExtension {

  HTML,
  JADE,
  MD,
  YAML,
  UNKNOWN;

  /** File extension including dot. */
  private final String extension;

  SiteFileType() {
    extension = new StringBuilder(name().length() + 1)
        .append('.')
        .append(name().toLowerCase(Locale.ENGLISH))
        .toString();
  }

  @Override
  public String appendTo(String value) {
    return new StringBuilder(value.length() + extension.length())
        .append(value)
        .append(extension)
        .toString();
  }

  @Override
  public boolean isPresent(Path path) {
    return isPresent(path.getFileName().toString());
  }

  @Override
  public boolean isPresent(String value) {
    return value.endsWith(extension);
  }

  @Override
  public String strip(String value) {
    return value.substring(0, value.length() - extension.length());
  }
}
