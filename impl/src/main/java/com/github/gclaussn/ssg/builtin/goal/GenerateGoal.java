package com.github.gclaussn.ssg.builtin.goal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.plugin.SitePluginGoal;

public class GenerateGoal implements SitePluginGoal {

  private static final Logger LOGGER = LoggerFactory.getLogger(GenerateGoal.class);

  @Override
  public int execute(Site site) {
    LOGGER.info("Generate site {}", site.getPath());

    if (!site.isLoaded() && !site.load().isEmpty()) {
      return SC_ERROR;
    }
    if (!site.generate().isEmpty()) {
      return SC_ERROR;
    }

    return SC_SUCCESS;
  }
}
