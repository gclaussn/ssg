package com.github.gclaussn.ssg.server.domain.event;

import javax.websocket.server.ServerEndpointConfig;

public class SiteEventEndpointConfigurator extends ServerEndpointConfig.Configurator {

  private final SiteEventEndpoint endpoint;

  public SiteEventEndpointConfigurator(SiteEventEndpoint endpoint) {
    this.endpoint = endpoint;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
    return (T) endpoint;
  }
}
