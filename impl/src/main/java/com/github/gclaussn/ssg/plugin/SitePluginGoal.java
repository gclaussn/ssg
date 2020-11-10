package com.github.gclaussn.ssg.plugin;

import com.github.gclaussn.ssg.Site;

public interface SitePluginGoal {

  static final int SC_SUCCESS = 0;
  static final int SC_ERROR = 1;

  int execute(Site site);
}
