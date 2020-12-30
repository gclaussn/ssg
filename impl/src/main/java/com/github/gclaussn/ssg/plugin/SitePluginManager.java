package com.github.gclaussn.ssg.plugin;

import java.util.Map;
import java.util.Set;

public interface SitePluginManager {

  void execute(SitePluginGoal pluginGoal);

  void execute(SitePluginGoal pluginGoal, Map<String, Object> properties);

  void execute(String pluginGoal);

  void execute(String pluginGoal, Map<String, Object> properties);

  Set<SitePluginDesc> getPlugins();

  SitePluginGoalDesc getPluginGoal(String pluginGoal);
}
