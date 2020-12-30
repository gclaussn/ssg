package com.github.gclaussn.ssg.server;

public class ServerDTO {

  private String host;
  private int port;
  private String webSocketUrl;

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getWebSocketUrl() {
    return webSocketUrl;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public void setWebSocketUrl(String webSocketUrl) {
    this.webSocketUrl = webSocketUrl;
  }
}
