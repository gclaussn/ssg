package com.github.gclaussn.ssg.impl;

import java.util.Optional;

import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.SiteErrorLocation;
import com.github.gclaussn.ssg.SiteErrorType;
import com.github.gclaussn.ssg.SiteException;
import com.github.gclaussn.ssg.Source;

class SiteErrorImpl implements SiteError {

  protected final Throwable cause;

  protected SiteErrorLocation location;
  protected String message;
  protected Source source;
  protected SiteErrorType type;

  SiteErrorImpl(Throwable cause) {
    this.cause = cause;
  }

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
}
