package com.github.gclaussn.ssg.cli.cmd;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.builtin.action.InitAction;
import com.github.gclaussn.ssg.cli.AbstractCommand;

@Parameters(commandNames = "init", commandDescription = "Initialize a new site from a predefined template")
public class Init extends AbstractCommand {

  @Parameter(description = "<template>", required = false)
  protected String template = "${SSG_HOME}/templates/default";

  @Override
  public void run(Site site) {
    InitAction.builder().template(template).execute(site);
  }
}
