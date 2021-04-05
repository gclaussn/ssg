package com.github.gclaussn.ssg.cli.cmd;

import com.beust.jcommander.Parameters;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.builtin.action.GenerateAction;
import com.github.gclaussn.ssg.cli.AbstractCommand;

@Parameters(commandNames = "generate", commandDescription = "Generate a site")
public class Generate extends AbstractCommand {

  @Override
  public void run(Site site) {
    site.getPluginManager().execute(new GenerateAction());
  }
}
