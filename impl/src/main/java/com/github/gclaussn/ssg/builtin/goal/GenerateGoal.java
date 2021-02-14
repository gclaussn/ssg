package com.github.gclaussn.ssg.builtin.goal;

import java.util.Collections;
import java.util.List;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.error.SiteError;
import com.github.gclaussn.ssg.plugin.SitePluginException;
import com.github.gclaussn.ssg.plugin.SitePluginGoal;

public class GenerateGoal implements SitePluginGoal {

  @Override
  public void execute(Site site) {
    List<SiteError> errors;

    errors = !site.isLoaded() ? site.load() : Collections.emptyList();
    if (!errors.isEmpty()) {
      throw new SitePluginException("Failed to load site");
    }

    errors = site.generate();
    if (!errors.isEmpty()) {
      throw new SitePluginException("Failed to generate site");
    }
  }
}
