package com.github.gclaussn.ssg.impl.plugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.conf.SiteConf;
import com.github.gclaussn.ssg.plugin.SitePlugin;
import com.github.gclaussn.ssg.plugin.SitePluginException;
import com.github.gclaussn.ssg.plugin.SitePluginGoal;
import com.github.gclaussn.ssg.plugin.SitePluginManager;

public class SitePluginManagerImpl implements SitePluginManager {

  public static SitePluginManager of(Site site, List<SitePlugin> plugins) {
    Objects.requireNonNull(site, "site is null");
    Objects.requireNonNull(plugins, "plugins are null");

    return new SitePluginManagerImpl(site).init(plugins);
  }

  /** Name of the default plugin. */
  private static final String DEFAULT_PLUGIN_NAME = "default";

  private final Site site;
  private final SiteConf siteConf;

  private final Map<String, SitePluginDescImpl> pluginMap;

  SitePluginManagerImpl(Site site) {
    this.site = site;
    this.siteConf = site.getConf();

    pluginMap = new HashMap<>();
  }

  @Override
  public int execute(String pluginGoal) {
    Objects.requireNonNull(pluginGoal, "plugin goal is null");

    return execute(pluginGoal, Collections.emptyMap());
  }

  @Override
  public int execute(String pluginGoal, Map<String, Object> properties) {
    Objects.requireNonNull(pluginGoal, "plugin goal is null");
    Objects.requireNonNull(properties, "properties are null");

    String pluginName = extractPluginName(pluginGoal);
    
    SitePluginDescImpl desc = pluginMap.get(pluginName);
    if (desc == null) {
      throw new RuntimeException(String.format("plugin '%s' could not be found", pluginName));
    }
    
    String pluginGoalName = extractPluginGoalName(pluginGoal);

    try {
      return execute(desc.findPluginGoal(pluginGoalName), properties);
    } catch (Exception e) {
      throw new SitePluginException(String.format("execution of plugin goal '%s' failed", pluginGoal), e);
    }
  }

  protected int execute(SitePluginGoal pluginGoal, Map<String, Object> properties) {
    // inject properties
    siteConf.inject(pluginGoal, properties);

    // execute goal
    return pluginGoal.execute(site);
  }

  protected String extractPluginGoalName(String pluginGoal) {
    int index = pluginGoal.indexOf(':');
    if (index == -1) {
      return pluginGoal;
    } else {
      return pluginGoal.substring(index + 1);
    }
  }

  protected String extractPluginName(String pluginGoal) {
    int index = pluginGoal.indexOf(':');
    if (index == -1) {
      return DEFAULT_PLUGIN_NAME;
    } else {
      return pluginGoal.substring(0, index);
    }
  }

  protected SitePluginManagerImpl init(List<SitePlugin> plugins) {
    // inject and call postBuild hook at each plugin
    plugins.stream().map(siteConf::inject).forEach(plugin -> plugin.postBuild(site));

    // register plugins
    Set<String> ambiguousNames = new HashSet<>();
    for (SitePlugin plugin : plugins) {
      SitePluginDescImpl desc = new SitePluginDescImpl(plugin);

      Class<?> type = plugin.getClass();

      pluginMap.put(type.getName(), desc);

      String shortName = desc.getShortName();

      boolean ambiguous = pluginMap.put(desc.getShortName(), desc) != null;
      if (ambiguous) {
        ambiguousNames.add(shortName);
      }
    }

    ambiguousNames.forEach(pluginMap::remove);
    ambiguousNames.clear();

    return this;
  }
}
