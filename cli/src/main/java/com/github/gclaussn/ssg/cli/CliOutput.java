package com.github.gclaussn.ssg.cli;

import java.io.PrintStream;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.conf.SiteConsole;
import com.github.gclaussn.ssg.error.SiteError;
import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.event.SiteEventListener;
import com.github.gclaussn.ssg.event.SiteEventType;

public class CliOutput implements SiteConsole, SiteEventListener {

  private final PrintStream ps;

  private final boolean verbose;

  private final Set<SiteEventType> includedEventTypes;

  public CliOutput(boolean verbose) {
    this(System.out, verbose);
  }

  public CliOutput(PrintStream ps, boolean verbose) {
    this.ps = ps;
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
    }
  }

  @Override
  public void log(String message) {
    ps.println(message);
  }

  @Override
  public void log(String format, Object... args) {
    ps.println(String.format(format, args));
  }

  protected void logError(SiteError error) {
    log(error.toMessage(verbose));

    if (verbose) {
      error.getCause().printStackTrace(ps);
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
      log(type.name());
      return;
    }

    Optional<String> reference = event.getReference();
    if (reference.isEmpty() || type == SiteEventType.LOAD_PAGE) {
      log(String.format("%s: %s", type, source.get().getId()));
    } else {
      log(String.format("%s: %s:%s", type, source.get().getId(), reference.get()));
    }

    if (error.isPresent()) {
      logError(error.get());
    }
  }
}
