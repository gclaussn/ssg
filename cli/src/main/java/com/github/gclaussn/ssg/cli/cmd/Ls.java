package com.github.gclaussn.ssg.cli.cmd;

import com.beust.jcommander.Parameters;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.builtin.action.LsAction;
import com.github.gclaussn.ssg.cli.AbstractCommand;

@Parameters(commandNames = "ls", commandDescription = "List the site's output")
public class Ls extends AbstractCommand {

  @Override
  public void run(Site site) {
    site.getPluginManager().execute(new LsAction());
  }
}
