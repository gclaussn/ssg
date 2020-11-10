package com.github.gclaussn.ssg.builtin.goal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.conf.SiteProperty;
import com.github.gclaussn.ssg.plugin.SitePluginGoal;

/**
 * Initializes a new site from a predefined template.
 */
public class InitGoal implements SitePluginGoal {

  private static final Logger LOGGER = LoggerFactory.getLogger(InitGoal.class);

  /** Locator for templates coming from classpath. */
  public static final String CLASSPATH_LOCATOR = "classpath:";

  /** Location of the template, used during initialization. */
  @SiteProperty(name = "ssg.init.template", defaultValue = "${SSG_HOME}/templates/default")
  protected String template;

  @Override
  public int execute(Site site) {
    LOGGER.info("Initializing site {}", site.getPath());

    if (template.startsWith(CLASSPATH_LOCATOR)) {
      new InitFromClasspath(template).execute(site);
    } else {
      new InitFromFile(template).execute(site);
    }

    return SC_SUCCESS;
  }
}
