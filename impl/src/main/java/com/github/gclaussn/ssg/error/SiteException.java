package com.github.gclaussn.ssg.error;

public class SiteException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private SiteError error;

  public SiteException(SiteError error) {
    super(error.getMessage(), error.getCause());
    this.error = error;
  }

  public SiteException(String message) {
    super(message);
  }

  public SiteException(String format, Object... args) {
    super(String.format(format, args));
  }

  public SiteError getError() {
    return error;
  }
}
