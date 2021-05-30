package com.github.gclaussn.ssg.cli.cmd;

import java.util.HashMap;
import java.util.Map;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteBuilder;
import com.github.gclaussn.ssg.cli.AbstractCommand;
import com.github.gclaussn.ssg.cli.Main;
import com.github.gclaussn.ssg.server.StartAction;
import com.github.gclaussn.ssg.server.domain.event.SiteEventLogger;

@Parameters(commandNames = "server", commandDescription = "Run a developer server")
public class Server extends AbstractCommand {

  @Parameter(names = {"--host", "-h"}, description = "Host name")
  protected String host;
  @Parameter(names = {"--port", "-p"}, description = "Port number")
  protected Integer port;

  @Override
  public void preBuild(SiteBuilder builder, Main main) {
    builder.addEventListener(new SiteEventLogger(main.isVerbose()));
  }

  @Override
  public void run(Site site) {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put(StartAction.HOST, host);
    properties.put(StartAction.PORT, port);

    site.getPluginManager().execute(new StartAction(), properties);
  }
}
