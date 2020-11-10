package com.github.gclaussn.ssg.server;

import java.util.Arrays;
import java.util.List;

import com.github.gclaussn.ssg.SiteBuilder;
import com.github.gclaussn.ssg.plugin.SitePlugin;
import com.github.gclaussn.ssg.plugin.SitePluginGoal;
import com.github.gclaussn.ssg.server.domain.event.SiteEventEndpoint;
import com.github.gclaussn.ssg.server.goal.StartGoal;

public class ServerPlugin implements SitePlugin {

  protected final SiteEventEndpoint eventEndpoint;

  public ServerPlugin() {
    eventEndpoint = new SiteEventEndpoint();
  }

  @Override
  public List<SitePluginGoal> getGoals() {
    return Arrays.asList(new StartGoal(eventEndpoint));
  }

  @Override
  public void preBuild(SiteBuilder builder) {
    // register event WebSocket endpoint as event listener
    builder.addEventListener(eventEndpoint);
  }
}
