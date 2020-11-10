package com.github.gclaussn.ssg.server.domain.event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnOpen;
import javax.websocket.Session;

import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.event.SiteEventListener;

public class SiteEventEndpoint implements SiteEventListener {

  private final Map<String, Session> sessions;

  public SiteEventEndpoint() {
    sessions = new ConcurrentHashMap<>();
  }

  @Override
  public void onEvent(SiteEvent event) {
    sessions.values().stream().filter(Session::isOpen).forEach(session -> publishEvent(event, session));
  }

  @OnOpen
  public void onOpen(Session session) {
    sessions.put(session.getId(), session);
  }

  protected void publishEvent(SiteEvent event, Session session) {
    session.getAsyncRemote().sendObject(event);
  }
}
