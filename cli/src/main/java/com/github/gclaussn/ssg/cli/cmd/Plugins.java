package com.github.gclaussn.ssg.cli.cmd;

import com.beust.jcommander.Parameters;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.cli.AbstractCommand;
import com.github.gclaussn.ssg.cli.CliOutputBuilder;
import com.github.gclaussn.ssg.conf.SitePropertyDesc;
import com.github.gclaussn.ssg.plugin.SitePluginDesc;

@Parameters(commandNames = "plugins", commandDescription = "Describe available plugins")
public class Plugins extends AbstractCommand {

  private CliOutputBuilder builder;

  @Override
  public void run(Site site) {
    for (SitePluginDesc desc : site.getPluginManager().getPlugins()) {
      builder = new CliOutputBuilder();

      append(desc);

      site.getConfiguration().getConsole().log(builder.toString());
    }
  }

  protected void append(SitePluginDesc desc) {
    builder.append(desc.getTypeName()).newLine();

    builder.inc();

    if (desc.getName() != null) {
      builder.indent().append(desc.getName()).newLine();
    }

    if (desc.getDocumentation() != null) {
      builder.newLine().indent().appendWrapped(desc.getDocumentation()).newLine();
    }

    if (!desc.getActions().isEmpty()) {
      builder.newLine().indent().append("Actions:");

      builder.inc();
      desc.getActions().forEach(this::append);
      builder.dec();
    }

    if (!desc.getProperties().isEmpty()) {
      builder.newLine().indent().append("Properties:");

      builder.inc();
      desc.getProperties().forEach(this::append);
      builder.dec();
    }

    builder.newLine();
  }

  protected void append(String action) {
    builder.newLine().indent().append(action);
  }

  protected void append(SitePropertyDesc desc) {
    builder.newLine().indent().append(desc.getName());
  }
}
