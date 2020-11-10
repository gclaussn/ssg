package com.github.gclaussn.ssg.event;

import java.util.Optional;
import java.util.function.Consumer;

import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.Source;

public interface SiteEvent {

  Optional<SiteError> getError();

  Optional<String> getReference();

  Optional<Source> getSource();

  long getTimestamp();

  SiteEventType getType();

  SiteEvent publish(Consumer<SiteEvent> consumer);

  SiteEvent with(SiteError error);
}
