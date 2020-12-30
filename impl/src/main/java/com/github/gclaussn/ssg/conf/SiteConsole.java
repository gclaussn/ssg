package com.github.gclaussn.ssg.conf;

public interface SiteConsole {

  /** Name of the property, used to overwrite the default console. */
  static final String PROPERTY_NAME = "ssg.console";

  void log(String message);

  void log(String format, Object... args);
}
