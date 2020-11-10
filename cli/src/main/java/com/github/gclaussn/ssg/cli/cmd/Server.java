package com.github.gclaussn.ssg.cli.cmd;

import java.util.HashMap;
import java.util.Map;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.cli.AbstractCmd;
import com.github.gclaussn.ssg.server.goal.StartGoal;

@Parameters(commandNames = "server", commandDescription = "Run a developer server")
public class Server extends AbstractCmd {

  @Parameter(names = {"--host", "-h"}, description = "Host name")
  protected String host;
  @Parameter(names = {"--port", "-p"}, description = "Port number")
  protected Integer port;

  @Override
  public int run(Site site, JCommander jc) {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put(StartGoal.HOST, host);
    properties.put(StartGoal.PORT, port);

    return site.execute("server:start", properties);
  }
}
