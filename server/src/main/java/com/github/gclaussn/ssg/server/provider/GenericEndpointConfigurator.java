package com.github.gclaussn.ssg.server.provider;

import javax.websocket.server.ServerEndpointConfig;

/**
 * Generic configurator for web socket endpoints. The configurator takes any web socket endpoint
 * implementation and casts it to the expected type.
 */
public class GenericEndpointConfigurator extends ServerEndpointConfig.Configurator {

  private final Object endpoint;

  public GenericEndpointConfigurator(Object endpoint) {
    this.endpoint = endpoint;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
    return (T) endpoint;
  }
}
