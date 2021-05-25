package com.github.gclaussn.ssg.server;

import com.github.gclaussn.ssg.SiteBuilder;
import com.github.gclaussn.ssg.plugin.SitePlugin;
import com.github.gclaussn.ssg.server.domain.event.SiteEventEndpoint;

public class ServerPlugin implements SitePlugin {

  public static final String SITE_EVENT_ENDPOINT = "ssg.server.siteEventEndpoint";

  protected final SiteEventEndpoint eventEndpoint;

  public ServerPlugin() {
    eventEndpoint = new SiteEventEndpoint();
  }

  @Override
  public void preBuild(SiteBuilder builder) {
    // register (Websocket) event endpoint as listener
    builder.addEventListener(eventEndpoint);
    builder.addFileEventListener(eventEndpoint);

    // make websocket endpoint availabe for the start action
    builder.setProperty(SITE_EVENT_ENDPOINT, eventEndpoint);

    // register actions
    builder.addPluginAction(StartAction.class);
  }
}
