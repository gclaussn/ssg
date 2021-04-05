package com.github.gclaussn.ssg.builtin.action;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.conf.SiteConsole;
import com.github.gclaussn.ssg.conf.SiteProperty;
import com.github.gclaussn.ssg.plugin.SitePluginAction;

/**
 * Initializes a new site from a predefined template.
 */
public class InitAction implements SitePluginAction {

  /** Locator for templates coming from classpath. */
  public static final String CLASSPATH_LOCATOR = "classpath:";

  protected SiteConsole console;

  /** Location of the template, used during initialization. */
  @SiteProperty(name = "ssg.init.template", defaultValue = "${SSG_HOME}/templates/default")
  protected String template;

  @Override
  public void execute(Site site) {
    console.log("Initializing site %s", site.getPath());

    if (template.startsWith(CLASSPATH_LOCATOR)) {
      InitFromClasspath init = new InitFromClasspath(template);
      init.console = console;
      init.execute(site);
    } else {
      InitFromFile init = new InitFromFile(template);
      init.console = console;
      init.execute(site);
    }
  }
}
