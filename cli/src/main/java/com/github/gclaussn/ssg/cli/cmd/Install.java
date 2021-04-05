package com.github.gclaussn.ssg.cli.cmd;

import com.beust.jcommander.Parameters;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.builtin.action.InstallAction;
import com.github.gclaussn.ssg.cli.AbstractCommand;

@Parameters(commandNames = "install", commandDescription = "Install specified node packages")
public class Install extends AbstractCommand {

  @Override
  public void run(Site site) {
    site.getPluginManager().execute(new InstallAction());
  }
}
