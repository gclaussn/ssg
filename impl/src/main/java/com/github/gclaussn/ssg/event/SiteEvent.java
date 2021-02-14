package com.github.gclaussn.ssg.event;

import java.util.Optional;

import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.error.SiteError;
import com.github.gclaussn.ssg.impl.event.SiteEventBuilderImpl;

public interface SiteEvent {

  static SiteEventBuilder builder() {
    return new SiteEventBuilderImpl();
  }

  Optional<SiteError> getError();

  Optional<String> getReference();

  Optional<Source> getSource();

  long getTimestamp();

  SiteEventType getType();
}
