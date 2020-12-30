package com.github.gclaussn.ssg.impl.plugin;

import java.util.HashSet;
import java.util.Set;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.plugin.SitePlugin;
import com.github.gclaussn.ssg.plugin.SitePluginGoal;

class SitePluginHolder {

  private final SitePlugin plugin;

  private final Set<Class<? extends SitePluginGoal>> pluginGoalTypes;

  SitePluginHolder(SitePlugin plugin) {
    this.plugin = plugin;

    pluginGoalTypes = new HashSet<>(4);
  }

  protected void addPluginGoal(Class<? extends SitePluginGoal> pluginGoalType) {
    pluginGoalTypes.add(pluginGoalType);
  }

  protected SitePlugin getPlugin() {
    return plugin;
  }

  protected Set<Class<? extends SitePluginGoal>> getPluginGoalTypes() {
    return pluginGoalTypes;
  }

  protected void postBuild(Site site) {
    site.getConf().inject(plugin);

    plugin.postBuild(site);
  }

  protected void preDestroy(Site site) {
    plugin.preDestroy(site);
  }
}
