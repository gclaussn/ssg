package com.github.gclaussn.ssg.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.github.gclaussn.ssg.Site;

public abstract class AbstractCmd implements CliConstants {

  @Parameter(names = {"--help"}, description = "Display this help", help = true)
  protected boolean help = false;

  public Site build(Main main) {
    return Site.builder()
        .addEventListener(new SiteEventLogger(main.isVerbose()))
        .setPropertyMap(main.getProperties())
        .build(main.getSitePath());
  }

  public boolean isHelp() {
    return help;
  }

  public abstract int run(Site site, JCommander jc);
}
