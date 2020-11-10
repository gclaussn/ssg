package com.github.gclaussn.ssg.plugin;

import java.util.List;
import java.util.Map;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.impl.plugin.SitePluginManagerImpl;

public interface SitePluginManager {

  static SitePluginManager of(Site site, List<SitePlugin> plugins) {
    return SitePluginManagerImpl.of(site, plugins);
  }

  int execute(String pluginGoal);

  int execute(String pluginGoal, Map<String, Object> properties);
}
