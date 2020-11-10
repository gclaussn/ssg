package com.github.gclaussn.ssg;

import java.util.Optional;

public interface SiteError {

  Throwable getCause();

  Optional<SiteErrorLocation> getLocation();

  String getMessage();

  Optional<Source> getSource();

  SiteErrorType getType();

  /**
   * Converts the error into an {@link SiteException}, which contains the error.
   * 
   * @return The newly created exception.
   * 
   * @see SiteException#getError()
   */
  SiteException toException();
}
