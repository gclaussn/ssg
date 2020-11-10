package com.github.gclaussn.ssg.cli.cmd;

import java.util.Iterator;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.internal.Console;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteOutput;
import com.github.gclaussn.ssg.cli.AbstractCmd;
import com.github.gclaussn.ssg.cli.Main;

@Parameters(commandNames = "ls", commandDescription = "Lists the site's output")
public class ListOutput extends AbstractCmd {

  @Override
  public Site build(Main main) {
    // build without SiteEventLogger
    return Site.builder().setPropertyMap(main.getProperties()).build(main.getSitePath());
  }

  @Override
  public int run(Site site, JCommander jc) {
    Console console = jc.getConsole();

    Iterator<SiteOutput> it = site.serve().iterator();
    while (it.hasNext()) {
      SiteOutput output = it.next();

      console.println(output.getPath());
    }

    return SC_SUCCESS;
  }
}
