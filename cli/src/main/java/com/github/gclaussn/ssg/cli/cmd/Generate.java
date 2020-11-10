package com.github.gclaussn.ssg.cli.cmd;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameters;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.cli.AbstractCmd;

@Parameters(commandNames = "generate", commandDescription = "Generate a site")
public class Generate extends AbstractCmd {

  @Override
  public int run(Site site, JCommander jc) {
    return site.execute("default:generate");
  }
}
