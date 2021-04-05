package com.github.gclaussn.ssg.plugin;

import java.util.Map;
import java.util.Set;

public interface SitePluginManager {

  void execute(SitePluginAction pluginAction);

  void execute(SitePluginAction pluginAction, Map<String, Object> properties);

  void execute(String pluginAction);

  void execute(String pluginAction, Map<String, Object> properties);

  Set<SitePluginDesc> getPlugins();

  SitePluginActionDesc getPluginAction(String pluginAction);
}
