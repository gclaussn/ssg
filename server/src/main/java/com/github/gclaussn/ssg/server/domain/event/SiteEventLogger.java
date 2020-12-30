package com.github.gclaussn.ssg.server.domain.event;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.event.SiteEventListener;
import com.github.gclaussn.ssg.event.SiteEventType;

public class SiteEventLogger implements SiteEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(SiteEventLogger.class);

  private final boolean verbose;

  private final Set<SiteEventType> includedEventTypes;

  public SiteEventLogger(boolean verbose) {
    this.verbose = verbose;

    if (verbose) {
      includedEventTypes = EnumSet.allOf(SiteEventType.class);
    } else {
      includedEventTypes = EnumSet.noneOf(SiteEventType.class);
      includedEventTypes.add(SiteEventType.GENERATE_PAGE);
      includedEventTypes.add(SiteEventType.GENERATE_PAGE_SET);
      includedEventTypes.add(SiteEventType.GENERATE_SITE);
      includedEventTypes.add(SiteEventType.LOAD_PAGE);
      includedEventTypes.add(SiteEventType.LOAD_PAGE_INCLUDE);
      includedEventTypes.add(SiteEventType.LOAD_PAGE_SET);
      includedEventTypes.add(SiteEventType.LOAD_SITE);

      // when running a file watcher
      includedEventTypes.add(SiteEventType.CREATE_JADE);
      includedEventTypes.add(SiteEventType.CREATE_YAML);
      includedEventTypes.add(SiteEventType.MODIFY_JADE);
      includedEventTypes.add(SiteEventType.MODIFY_YAML);
      includedEventTypes.add(SiteEventType.DELETE_JADE);
      includedEventTypes.add(SiteEventType.DELETE_YAML);
    }
  }

  @Override
  public void onEvent(SiteEvent event) {
    SiteEventType type = event.getType();
    Optional<SiteError> error = event.getError();

    if (!includedEventTypes.contains(type) && error.isEmpty()) {
      // skip not included site event types
      // if not error has occurred
      return;
    }

    Optional<Source> source = event.getSource();
    if (source.isEmpty()) {
      LOGGER.info(type.name());
      return;
    }

    Optional<String> reference = event.getReference();
    if (reference.isEmpty() || type == SiteEventType.LOAD_PAGE) {
      LOGGER.info("{}: {}", type, source.get().getId());
    } else {
      LOGGER.info("{}: {}:{}", type, source.get().getId(), reference.get());
    }

    if (error.isPresent()) {
      logError(error.get());
    }
  }

  protected void logError(SiteError error) {
    String message = error.toMessage(verbose);

    if (verbose) {
      LOGGER.error(message, error.getCause());
    } else {
      LOGGER.error(message);
    }
  }
}
