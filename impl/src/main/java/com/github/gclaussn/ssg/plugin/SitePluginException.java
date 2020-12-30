package com.github.gclaussn.ssg.plugin;

public class SitePluginException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final int statusCode;

  public SitePluginException(String message) {
    this(message, 1, null);
  }

  public SitePluginException(String message, Throwable cause) {
    this(message, 1, cause);
  }

  public SitePluginException(String message, int statusCode, Throwable cause) {
    super(message, cause);

    this.statusCode = statusCode;
  }

  public int getStatusCode() {
    return statusCode;
  }
}
