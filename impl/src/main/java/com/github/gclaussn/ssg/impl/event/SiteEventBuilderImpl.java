package com.github.gclaussn.ssg.impl.event;

import java.util.Objects;
import java.util.function.Consumer;

import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.SourceType;
import com.github.gclaussn.ssg.error.SiteError;
import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.event.SiteEventBuilder;
import com.github.gclaussn.ssg.event.SiteEventType;
import com.github.gclaussn.ssg.impl.model.SourceImpl;

public class SiteEventBuilderImpl implements SiteEventBuilder {

  private SiteEventImpl event;

  public SiteEventBuilderImpl() {
    event = new SiteEventImpl();
  }

  @Override
  public SiteEvent build() {
    return event;
  }

  @Override
  public SiteEvent buildAndPublish(Consumer<SiteEvent> consumer) {
    Objects.requireNonNull(consumer, "consumer is null");
    consumer.accept(event);

    return event;
  }

  @Override
  public SiteEventBuilder error(SiteError error) {
    event.error = error;
    return this;
  }

  @Override
  public SiteEventBuilder reference(String reference) {
    event.reference = reference;
    return this;
  }

  @Override
  public SiteEventBuilder source(Source source) {
    event.source = source;
    return this;
  }

  @Override
  public SiteEventBuilder source(SourceType sourceType, String sourceId) {
    event.source = new SourceImpl(sourceType, sourceId);
    return this;
  }

  @Override
  public SiteEventBuilder type(SiteEventType type) {
    event.type = type;
    return this;
  }
}
