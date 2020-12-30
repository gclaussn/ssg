package com.github.gclaussn.ssg.cli.cmd;

import com.beust.jcommander.Parameters;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.builtin.goal.InitGoal;
import com.github.gclaussn.ssg.cli.AbstractCommand;

@Parameters(commandNames = "init", commandDescription = "Initialize a new site from a predefined template")
public class Init extends AbstractCommand {

  @Override
  public void run(Site site) {
    site.getPluginManager().execute(new InitGoal());
  }
}
