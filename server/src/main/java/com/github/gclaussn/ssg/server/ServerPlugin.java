package com.github.gclaussn.ssg.server;

import com.github.gclaussn.ssg.SiteBuilder;
import com.github.gclaussn.ssg.plugin.SitePlugin;
import com.github.gclaussn.ssg.server.domain.event.SiteEventEndpoint;

public class ServerPlugin implements SitePlugin {

  protected final SiteEventEndpoint eventEndpoint;

  public ServerPlugin() {
    eventEndpoint = new SiteEventEndpoint();
  }

  @Override
  public void preBuild(SiteBuilder builder) {
    // register event WebSocket endpoint as event listener
    builder.addEventListener(eventEndpoint);

    // make websocket endpoint availabe for the start action
    builder.setProperty(StartAction.SITE_EVENT_ENDPOINT, eventEndpoint);

    // register actions
    builder.addPluginAction(StartAction.class);
  }
}
