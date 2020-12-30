package com.github.gclaussn.ssg.cli.cmd;

import java.util.LinkedList;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.cli.AbstractCommand;
import com.github.gclaussn.ssg.plugin.SitePluginManager;

@Parameters(commandNames = "exec", commandDescription = "Execute plugin goals")
public class Exec extends AbstractCommand {

  @Parameter(description = "<plugin goals>", required = true)
  protected List<String> pluginGoals = new LinkedList<>();

  @Override
  public void run(Site site) {
    SitePluginManager pluginManager = site.getPluginManager();

    pluginGoals.forEach(pluginManager::execute);
  }
}
