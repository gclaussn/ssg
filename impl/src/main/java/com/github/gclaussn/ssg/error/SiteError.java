package com.github.gclaussn.ssg.error;

import java.util.Optional;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.impl.error.SiteErrorBuilderImpl;

public interface SiteError {

  static SiteErrorBuilder builder(Site site) {
    return new SiteErrorBuilderImpl(site);
  }

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

  String toMessage(boolean verbose);
}
