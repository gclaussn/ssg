package com.github.gclaussn.ssg.event;

import java.util.function.Consumer;

import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.SourceType;

public interface SiteEventBuilder {

  SiteEvent build();

  SiteEvent buildAndPublish(Consumer<SiteEvent> consumer);

  SiteEventBuilder error(SiteError error);

  SiteEventBuilder reference(String reference);

  SiteEventBuilder source(Source source);

  SiteEventBuilder source(SourceType sourceType, String sourceId);

  SiteEventBuilder type(SiteEventType type);
}
