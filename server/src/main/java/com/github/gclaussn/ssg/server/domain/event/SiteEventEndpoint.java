package com.github.gclaussn.ssg.server.domain.event;

import static com.github.gclaussn.ssg.file.SiteFileType.MD;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.event.SiteEventListener;
import com.github.gclaussn.ssg.file.SiteFileEvent;
import com.github.gclaussn.ssg.file.SiteFileEventListener;
import com.github.gclaussn.ssg.file.SiteFileType;
import com.github.gclaussn.ssg.server.domain.SiteErrorDTO;
import com.github.gclaussn.ssg.server.domain.SiteErrorLocationDTO;
import com.github.gclaussn.ssg.server.domain.SourceCodeDTO;
import com.github.gclaussn.ssg.server.domain.file.SiteFileEventDTO;

public class SiteEventEndpoint implements SiteEventListener, SiteFileEventListener, AutoCloseable {

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

  /**
   * Extracts the source code, which caused the error.
   * 
   * @param location The error location, that provides the file path and the line number.
   * 
   * @return The extracted source code, including the related code snippet.
   */
  protected SourceCodeDTO extractSourceCode(SiteErrorLocationDTO location) {
    SourceCodeDTO sourceCode = new SourceCodeDTO();
    sourceCode.setFileType(SiteFileType.of(location.getPath()));

    // read source code
    List<String> lines;
    try {
      lines = Files.readAllLines(location.getPath(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      sourceCode.setCode(e.getMessage());
      sourceCode.setFrom(-1);
      sourceCode.setTo(-1);

      return sourceCode;
    }

    // from and to represent line numbers not indices
    int from = location.getLine() > 10 ? (location.getLine() - 10) : 1;
    int to = location.getLine();

    if (sourceCode.getFileType() == MD && from == 1) {
      // due to Markdown front matter separator
      from += 1;
    }

    // build code snippet
    StringBuilder codeBuilder = new StringBuilder(512);
    for (int i = from; i <= to; i++) {
      codeBuilder.append(lines.get(i - 1));
      codeBuilder.append('\n');
    }

    sourceCode.setCode(codeBuilder.toString());
    sourceCode.setFrom(from);
    sourceCode.setTo(to);

    return sourceCode;
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
    SiteEventDTO data = SiteEventDTO.of(event);

    SiteErrorDTO error = data.getError();
    if (error != null && error.getLocation() != null) {
      // extract source code from location
      SourceCodeDTO sourceCode = extractSourceCode(error.getLocation());

      // enrich error
      error.setSourceCode(sourceCode);
    }

    sessions.values().stream().filter(Session::isOpen).forEach(session -> session.getAsyncRemote().sendObject(data));
  }

  @Override
  public void onEvent(SiteFileEvent event) {
    SiteFileEventDTO data = SiteFileEventDTO.of(event);

    sessions.values().stream().filter(Session::isOpen).forEach(session -> session.getAsyncRemote().sendObject(data));
  }
}
