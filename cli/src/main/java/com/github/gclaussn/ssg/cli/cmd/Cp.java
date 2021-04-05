package com.github.gclaussn.ssg.cli.cmd;

import java.util.HashMap;
import java.util.Map;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.builtin.action.CpAction;
import com.github.gclaussn.ssg.cli.AbstractCommand;

@Parameters(commandNames = "cp", commandDescription = "Copy the site's output")
public class Cp extends AbstractCommand {

  @Parameter(description = "<target>", required = true)
  protected String target;

  @Override
  public void run(Site site) {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put(CpAction.TARGET, target);

    site.getPluginManager().execute(new CpAction(), properties);
  }
}
