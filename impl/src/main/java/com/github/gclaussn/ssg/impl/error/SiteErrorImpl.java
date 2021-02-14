package com.github.gclaussn.ssg.impl.error;

import java.util.Optional;

import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.error.SiteError;
import com.github.gclaussn.ssg.error.SiteErrorLocation;
import com.github.gclaussn.ssg.error.SiteErrorType;
import com.github.gclaussn.ssg.error.SiteException;

class SiteErrorImpl implements SiteError {

  protected Throwable cause;
  protected SiteErrorLocation location;
  protected String message;
  protected Source source;
  protected SiteErrorType type;

  @Override
  public Throwable getCause() {
    return cause;
  }

  @Override
  public Optional<SiteErrorLocation> getLocation() {
    return Optional.ofNullable(location);
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public Optional<Source> getSource() {
    return Optional.ofNullable(source);
  }

  @Override
  public SiteErrorType getType() {
    return type;
  }

  @Override
  public SiteException toException() {
    return new SiteException(this);
  }

  @Override
  public String toMessage(boolean verbose) {
    StringBuilder sb = new StringBuilder();
    sb.append("Site error occurred");

    if (location != null) {
      sb.append(" in ");
      sb.append(location);
    }

    if (!verbose) {
      sb.append(":\n");
      sb.append(message);
      sb.append(":\n");
      sb.append(cause.getMessage());
    }

    return sb.toString();
  }
}
