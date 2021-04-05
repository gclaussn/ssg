package com.github.gclaussn.ssg.impl.plugin;

import java.util.HashSet;
import java.util.Set;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.plugin.SitePlugin;
import com.github.gclaussn.ssg.plugin.SitePluginAction;

class SitePluginHolder {

  private final SitePlugin plugin;

  private final Set<Class<? extends SitePluginAction>> pluginActionTypes;

  SitePluginHolder(SitePlugin plugin) {
    this.plugin = plugin;

    pluginActionTypes = new HashSet<>(4);
  }

  protected void addPluginAction(Class<? extends SitePluginAction> pluginActionType) {
    pluginActionTypes.add(pluginActionType);
  }

  protected SitePlugin getPlugin() {
    return plugin;
  }

  protected Set<Class<? extends SitePluginAction>> getPluginActionTypes() {
    return pluginActionTypes;
  }

  protected void postBuild(Site site) {
    site.getConfiguration().inject(plugin);

    plugin.postBuild(site);
  }

  protected void preDestroy(Site site) {
    plugin.preDestroy(site);
  }
}
