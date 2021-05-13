package com.github.gclaussn.ssg.server.domain.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gclaussn.ssg.file.SiteFileEvent;
import com.github.gclaussn.ssg.file.SiteFileEventListener;

public class SiteFileEventLogger implements SiteFileEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(SiteFileEventLogger.class);

  @Override
  public void onEvent(SiteFileEvent event) {
    LOGGER.info("{}: {}", event.getType(), event.getPath());
  }
}
