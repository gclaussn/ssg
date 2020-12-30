package com.github.gclaussn.ssg.cli;

import com.beust.jcommander.Parameter;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteBuilder;

public abstract class AbstractCommand {

  @Parameter(names = {"--help"}, description = "Display this help", help = true)
  protected boolean help = false;

  public boolean isHelp() {
    return help;
  }

  public void preBuild(SiteBuilder builder, Main main) {
    CliOutput output = new CliOutput(main.getPrintStream(), main.isVerbose());

    builder.addEventListener(output);
    builder.setConsole(output);
  }

  public abstract void run(Site site);
}
