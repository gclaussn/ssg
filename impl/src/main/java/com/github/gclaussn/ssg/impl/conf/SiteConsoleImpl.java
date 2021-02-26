package com.github.gclaussn.ssg.impl.conf;

import java.io.PrintStream;

import com.github.gclaussn.ssg.conf.SiteConsole;

/**
 * Default console implementation, based on a print stream.
 */
class SiteConsoleImpl implements SiteConsole {

  private final PrintStream ps;

  SiteConsoleImpl() {
    this(System.out);
  }

  SiteConsoleImpl(PrintStream ps) {
    this.ps = ps;
  }

  @Override
  public void log(String message) {
    ps.println(message);
  }

  @Override
  public void log(String format, Object... args) {
    ps.println(String.format(format, args));
  }
}
