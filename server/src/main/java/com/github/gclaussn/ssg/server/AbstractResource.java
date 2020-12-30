package com.github.gclaussn.ssg.server;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.Status;

import com.github.gclaussn.ssg.Site;

public class AbstractResource {

  protected Site site;

  protected ServerDTO getServer(HttpHeaders httpHeaders) {
    String headerValue = httpHeaders.getHeaderString(HttpHeaders.HOST);
    if (headerValue == null) {
      throw new WebApplicationException(Status.BAD_REQUEST);
    }

    int index = headerValue.indexOf(':');

    ServerDTO server = new ServerDTO();
    server.setHost(headerValue.substring(0, index));
    server.setPort(Integer.parseInt(headerValue.substring(index + 1)));

    // build basic websocket url
    server.setWebSocketUrl(String.format("ws://%s:%d/wsa", server.getHost(), server.getPort()));

    return server;
  }

  protected void init(Site site) {
    this.site = site;
  }
}
