package com.github.gclaussn.ssg.server.domain.event;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.event.SiteEventListener;

public class SiteEventEndpoint implements SiteEventListener, AutoCloseable {

  private final Map<String, Session> sessions;

  public SiteEventEndpoint() {
    sessions = new ConcurrentHashMap<>();
  }

  @Override
  public void close() {
    Set<String> sessionIds = new HashSet<>(sessions.keySet());

    for (String sessionId : sessionIds) {
      try {
        sessions.get(sessionId).close();
      } catch (IOException e) {
        // ignore exception
      }
    }
  }

  @OnOpen
  public void onOpen(Session session) {
    sessions.put(session.getId(), session);
  }

  @OnClose
  public void onClose(Session session) {
    sessions.remove(session.getId());
  }

  @Override
  public void onEvent(SiteEvent event) {
    sessions.values().stream().filter(Session::isOpen).forEach(session -> publishEvent(event, session));
  }

  protected void publishEvent(SiteEvent event, Session session) {
    session.getAsyncRemote().sendObject(event);
  }
}
