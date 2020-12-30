package com.github.gclaussn.ssg.server.domain.plugin.goal;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.conf.SiteConsole;
import com.github.gclaussn.ssg.plugin.SitePluginException;

class SitePluginGoalTask implements Runnable, SiteConsole {

  private static final Logger LOGGER = LoggerFactory.getLogger(SitePluginGoalTask.class);

  protected Site site;

  protected String pluginGoal;
  protected Map<String, Object> properties;

  private final String id;

  private final LinkedBlockingQueue<String> messages;

  private volatile boolean done;

  SitePluginGoalTask() {
    id = RandomStringUtils.randomAlphabetic(8);
    messages = new LinkedBlockingQueue<>();
    done = false;
  }

  protected String getId() {
    return id;
  }

  protected LinkedBlockingQueue<String> getMessages() {
    return messages;
  }

  protected boolean isDone() {
    return done;
  }

  @Override
  public void log(String message) {
    messages.add(message);
  }

  @Override
  public void log(String format, Object... args) {
    log(String.format(format, args));
  }

  @Override
  public void run() {
    LOGGER.info("Task {}: {}: Started", id, pluginGoal);

    // overwrite console
    properties.put(SiteConsole.PROPERTY_NAME, this);

    try {
      long a = System.currentTimeMillis();
      site.getPluginManager().execute(pluginGoal, properties);
      long b = System.currentTimeMillis();

      log("\nTask succeeded in %dms", b - a);
    } catch (SitePluginException e) {
      log("\nTask failed with status code %d", e.getStatusCode());
      log(ExceptionUtils.getStackTrace(e));
    } catch (Exception e) {
      log("\nTask failed");
      log(ExceptionUtils.getStackTrace(e));
    }

    LOGGER.info("Task {}: {}: Finished", id, pluginGoal);
    done = true;
  }
}
