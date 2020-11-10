package com.github.gclaussn.ssg.impl.event;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.event.SiteEventType;

class SiteEventImpl implements SiteEvent {

  protected SiteError error;
  protected String reference;
  protected Source source;
  protected long timestamp;
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

  @Override
  public SiteEvent publish(Consumer<SiteEvent> consumer) {
    Objects.requireNonNull(consumer, "consumer is null");

    consumer.accept(this);
    return this;
  }

  @Override
  public SiteEvent with(SiteError error) {
    Objects.requireNonNull(error, "error is null");

    SiteEventImpl event = new SiteEventImpl();
    event.error = error;
    event.reference = reference;
    event.source = source;
    event.timestamp = timestamp;
    event.type = type;

    return event;
  }
}
