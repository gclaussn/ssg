package com.github.gclaussn.ssg.server.provider;

import java.util.Arrays;

import javax.websocket.server.ServerEndpointConfig;

/**
 * Generic web socket endpoint.
 */
public class GenericEndpoint extends ServerEndpointConfig.Configurator {

  public static ServerEndpointConfig of(Object endpoint, String path) {
    return ServerEndpointConfig.Builder.create(endpoint.getClass(), path)
        .configurator(new GenericEndpoint(endpoint))
        .encoders(Arrays.asList(GenericEncoder.class))
        .build();
  }

  private final Object endpoint;

  protected GenericEndpoint(Object endpoint) {
    this.endpoint = endpoint;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
    return (T) endpoint;
  }
}
