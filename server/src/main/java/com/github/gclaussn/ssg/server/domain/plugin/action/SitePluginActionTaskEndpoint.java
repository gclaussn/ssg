package com.github.gclaussn.ssg.server.domain.plugin.action;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gclaussn.ssg.Site;

public class SitePluginActionTaskEndpoint implements ThreadFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(SitePluginActionTaskEndpoint.class);

  private static final String THREAD_NAME = "exec";

  private final ExecutorService executorService;

  private final Map<String, SitePluginActionTask> tasks;

  public SitePluginActionTaskEndpoint() {
    executorService = Executors.newSingleThreadExecutor(this);
    tasks = new ConcurrentHashMap<>();
  }

  @Override
  public Thread newThread(Runnable r) {
    return new Thread(r, THREAD_NAME);
  }

  @OnOpen
  public void onOpen(Session session) {
    LOGGER.info("Session for {}: Opened", session.getRequestURI());

    // parse querystring e.g. "id=luPGYdaD" and find task ID
    Optional<String> taskId = URLEncodedUtils.parse(session.getQueryString(), StandardCharsets.UTF_8).stream()
        .filter(nameValuePair -> nameValuePair.getName().equals("id"))
        .map(NameValuePair::getValue)
        .findFirst();

    sendMessages(session, taskId);

    try {
      session.close();
    } catch (IOException e) {
      // ignore exception
    }

    LOGGER.info("Session for {}: Closed", session.getRequestURI());
  }

  public void stop() {
    try {
      executorService.shutdown();
      executorService.awaitTermination(30L, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      // ignore exception
    }
  }

  protected void sendMessages(Session session, Optional<String> taskId) {
    if (taskId.isEmpty()) {
      return;
    }

    SitePluginActionTask task = tasks.get(taskId.get());
    if (task == null) {
      return;
    }

    LinkedBlockingQueue<String> messages = task.getMessages();
    try {
      while (!messages.isEmpty() || !task.isDone()) {
        String message = messages.poll(400L, TimeUnit.MILLISECONDS);
        if (message == null) {
          continue;
        }

        session.getBasicRemote().sendText(message);
      }
    } catch (InterruptedException e) {
      // ignore exception
    } catch (IOException e) {
      LOGGER.error("Session {}: Failed to send messages", session.getId(), e);
    } finally {
      messages.clear();
    }

    tasks.remove(task.getId());
  }

  public String submit(Site site, String pluginAction, Map<String, Object> properties) {
    SitePluginActionTask task = new SitePluginActionTask();
    task.site = site;
    task.pluginAction = pluginAction;
    task.properties = properties;

    tasks.put(task.getId(), task);

    executorService.submit(task);
    LOGGER.info("Task {}: {}: Submitted", task.getId(), pluginAction);

    return task.getId();
  }
}
