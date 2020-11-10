package com.github.gclaussn.ssg.server.domain.event;

import java.util.Optional;

import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.SourceType;
import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.event.SiteEventType;
import com.github.gclaussn.ssg.server.domain.SiteErrorDTO;

public class SiteEventDTO {

  public static SiteEventDTO of(SiteEvent event) {
    Optional<Source> source = event.getSource();
    
    SiteEventDTO target = new SiteEventDTO();
    target.error = event.getError().map(SiteErrorDTO::of).orElse(null);
    target.reference = event.getReference().orElse(null);
    target.sourceId = source.map(Source::getId).orElse(null);
    target.sourceType = source.map(Source::getType).orElse(null);
    target.timestamp = event.getTimestamp();
    target.type = event.getType();
    
    return target;
  }

  private SiteErrorDTO error;
  private String reference;
  private String sourceId;
  private SourceType sourceType;
  private long timestamp;
  private SiteEventType type;

  public SiteErrorDTO getError() {
    return error;
  }

  public String getReference() {
    return reference;
  }

  public String getSourceId() {
    return sourceId;
  }

  public SourceType getSourceType() {
    return sourceType;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public SiteEventType getType() {
    return type;
  }
}
