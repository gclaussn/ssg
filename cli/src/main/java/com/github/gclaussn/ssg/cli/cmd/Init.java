package com.github.gclaussn.ssg.cli.cmd;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameters;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.cli.AbstractCmd;

@Parameters(commandNames = "init", commandDescription = "Initialize a new site from a predefined template")
public class Init extends AbstractCmd {

  @Override
  public int run(Site site, JCommander jc) {
    return site.execute("default:init");
  }
}
