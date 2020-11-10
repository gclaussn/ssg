package com.github.gclaussn.ssg.cli.cmd;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.cli.AbstractCmd;

@Parameters(commandNames = "execute", commandDescription = "Execute a plugin goal")
public class Execute extends AbstractCmd {

  @Parameter(description = "<plugin goal>", required = true)
  protected String pluginGoal;

  @Override
  public int run(Site site, JCommander jc) {
    return site.execute(pluginGoal);
  }
}
