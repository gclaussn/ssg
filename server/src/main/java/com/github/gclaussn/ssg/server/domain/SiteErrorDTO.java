package com.github.gclaussn.ssg.server.domain;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.SiteErrorType;

public class SiteErrorDTO {

  public static SiteErrorDTO of(SiteError error) {
    SiteErrorDTO target = new SiteErrorDTO();
    target.cause = error.getCause().getMessage();
    target.location = error.getLocation().map(SiteErrorLocationDTO::of).orElse(null);
    target.message = error.getMessage();
    target.stackTrace = ExceptionUtils.getStackTrace(error.getCause());
    target.type = error.getType();

    return target;
  }

  private String cause;
  private SiteErrorLocationDTO location;
  private String message;
  private SourceCodeDTO sourceCode;
  private String stackTrace;
  private SiteErrorType type;

  public String getCause() {
    return cause;
  }

  public SiteErrorLocationDTO getLocation() {
    return location;
  }

  public String getMessage() {
    return message;
  }

  public SourceCodeDTO getSourceCode() {
    return sourceCode;
  }

  public String getStackTrace() {
    return stackTrace;
  }

  public SiteErrorType getType() {
    return type;
  }

  public void setSourceCode(SourceCodeDTO sourceCode) {
    this.sourceCode = sourceCode;
  }
}
