package com.github.gclaussn.ssg.impl.plugin;

import java.util.HashMap;
import java.util.Map;

import com.github.gclaussn.ssg.plugin.SitePlugin;
import com.github.gclaussn.ssg.plugin.SitePluginDesc;
import com.github.gclaussn.ssg.plugin.SitePluginGoal;

class SitePluginDescImpl implements SitePluginDesc {

  private static final String PLUGIN_SUFFIX = "Plugin";
  private static final String PLUGIN_GOAL_SUFFIX = "Goal";

  private final String shortName;

  private final Map<String, SitePluginGoal> goals;

  SitePluginDescImpl(SitePlugin plugin) {
    shortName = extractShortName(plugin.getClass().getSimpleName());

    goals = new HashMap<>();
    for (SitePluginGoal goal : plugin.getGoals()) {
      Class<?> type = goal.getClass();

      String shortName = extractGoalShortName(type.getSimpleName());

      goals.put(shortName, goal);
    }
  }

  private String convertName(String simpleName, String suffix) {
    String shortName;
    if (simpleName.endsWith(suffix) && simpleName.length() > suffix.length()) {
      shortName = simpleName.substring(0, simpleName.length() - suffix.length());
    } else {
      shortName = simpleName;
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < shortName.length(); i++) {
      char c = shortName.charAt(i);

      if (Character.isUpperCase(c)) {
        sb.append(sb.length() != 0 ? "-" : "");
        sb.append(Character.toLowerCase(c));
      } else {
        sb.append(c);
      }
    }

    return sb.toString();
  }

  protected String extractGoalShortName(String simpleName) {
    return convertName(simpleName, PLUGIN_GOAL_SUFFIX);
  }

  protected String extractShortName(String simpleName) {
    return convertName(simpleName, PLUGIN_SUFFIX);
  }

  protected SitePluginGoal findPluginGoal(String pluginGoalName) {
    SitePluginGoal goal = goals.get(pluginGoalName);
    if (goal == null) {
      throw new RuntimeException(String.format("plugin goal '%s' could not be found", pluginGoalName));
    }
    return goal;
  }

  @Override
  public String getShortName() {
    return shortName;
  }
}
