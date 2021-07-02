package com.github.gclaussn.ssg.builtin.action;

import java.util.Collections;
import java.util.List;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.plugin.SitePluginAction;
import com.github.gclaussn.ssg.plugin.SitePluginException;

public class GenerateAction implements SitePluginAction {

  @Override
  public void execute(Site site) {
    List<SiteError> errors;

    errors = isEmpty(site) ? site.load() : Collections.emptyList();
    if (!errors.isEmpty()) {
      throw new SitePluginException("Failed to load site");
    }

    errors = site.generate();
    if (!errors.isEmpty()) {
      throw new SitePluginException("Failed to generate site");
    }
  }

  protected boolean isEmpty(Site site) {
    return site.getPages().isEmpty() && site.getPageSets().isEmpty();
  }
}
