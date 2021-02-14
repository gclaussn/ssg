package com.github.gclaussn.ssg.server.domain;

import java.util.Optional;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.SourceType;
import com.github.gclaussn.ssg.error.SiteError;
import com.github.gclaussn.ssg.error.SiteErrorType;

public class SiteErrorDTO {

  public static SiteErrorDTO of(SiteError error) {
    Optional<Source> source = error.getSource();

    SiteErrorDTO target = new SiteErrorDTO();
    target.cause = error.getCause().getMessage();
    target.location = error.getLocation().map(SiteErrorLocationDTO::of).orElse(null);
    target.message = error.getMessage();
    target.sourceId = source.map(Source::getId).orElse(null);
    target.sourceType = source.map(Source::getType).orElse(null);
    target.stackTrace = ExceptionUtils.getStackTrace(error.getCause());
    target.type = error.getType();

    return target;
  }

  private String cause;
  private SiteErrorLocationDTO location;
  private String message;
  private String sourceId;
  private SourceType sourceType;
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

  public String getSourceId() {
    return sourceId;
  }

  public SourceType getSourceType() {
    return sourceType;
  }

  public String getStackTrace() {
    return stackTrace;
  }

  public SiteErrorType getType() {
    return type;
  }
}
