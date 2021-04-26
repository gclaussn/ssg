package com.github.gclaussn.ssg.impl.event;

import java.time.Instant;
import java.util.Optional;

import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.event.SiteEventType;

class SiteEventImpl implements SiteEvent {

  protected final long timestamp;

  protected SiteError error;
  protected String reference;
  protected Source source;
  protected SiteEventType type;

  SiteEventImpl() {
    timestamp = Instant.now().toEpochMilli();
  }

  @Override
  public Optional<SiteError> getError() {
    return Optional.ofNullable(error);
  }

  @Override
  public Optional<String> getReference() {
    return Optional.ofNullable(reference);
  }

  @Override
  public Optional<Source> getSource() {
    return Optional.ofNullable(source);
  }

  @Override
  public long getTimestamp() {
    return timestamp;
  }

  @Override
  public SiteEventType getType() {
    return type;
  }
}
