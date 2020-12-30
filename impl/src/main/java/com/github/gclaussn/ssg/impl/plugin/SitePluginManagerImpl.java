package com.github.gclaussn.ssg.impl.plugin;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteBuilder;
import com.github.gclaussn.ssg.conf.TypeDesc;
import com.github.gclaussn.ssg.plugin.SitePlugin;
import com.github.gclaussn.ssg.plugin.SitePluginDesc;
import com.github.gclaussn.ssg.plugin.SitePluginException;
import com.github.gclaussn.ssg.plugin.SitePluginGoal;
import com.github.gclaussn.ssg.plugin.SitePluginGoalDesc;
import com.github.gclaussn.ssg.plugin.SitePluginManager;

public class SitePluginManagerImpl implements SitePluginManager, SitePlugin {

  /** Common suffix of {@link SitePluginGoal} implementations. */
  protected static final String PLUGIN_GOAL_SUFFIX = "Goal";

  private final List<SitePluginHolder> plugins;

  private final Set<Class<? extends SitePluginGoal>> pluginGoalTypes;

  private final Map<String, Class<? extends SitePluginGoal>> pluginGoalTypeMap;

  private Site site;

  public SitePluginManagerImpl() {
    plugins = new ArrayList<>();
    pluginGoalTypes = new HashSet<>();
    pluginGoalTypeMap = new HashMap<>();

  }

  protected void addPlugin(SitePlugin plugin) {
    plugins.add(new SitePluginHolder(plugin));
  }

  public void addPluginGoal(Class<? extends SitePluginGoal> pluginGoalType) {
    if (plugins.isEmpty()) {
      return;
    }

    plugins.get(plugins.size() - 1).addPluginGoal(pluginGoalType);
    pluginGoalTypes.add(pluginGoalType);
  }

  protected SitePluginDesc buildPluginDesc(SitePluginHolder holder) {
    Class<?> type = holder.getPlugin().getClass();

    TypeDesc typeDesc = site.getConf().describe(type);

    SitePluginDescImpl desc = new SitePluginDescImpl();
    desc.documentation = typeDesc.getDocumentation();
    desc.name = typeDesc.getName();
    desc.properties.addAll(typeDesc.getProperties());
    desc.typeName = type.getName();

    holder.getPluginGoalTypes().stream().map(Class::getName).forEach(desc.goals::add);

    return desc;
  }

  protected SitePluginGoalDesc buildPluginGoalDesc(Class<? extends SitePluginGoal> pluginGoalType) {
    TypeDesc typeDesc = site.getConf().describe(pluginGoalType);

    SitePluginGoalDescImpl desc = new SitePluginGoalDescImpl();
    desc.documentation = typeDesc.getDocumentation();
    desc.name = typeDesc.getName();
    desc.id = buildPluginGoalId(pluginGoalType.getSimpleName());
    desc.properties = typeDesc.getProperties();
    desc.typeName = pluginGoalType.getName();

    return desc;
  }

  protected String buildPluginGoalId(String simpleName) {
    if (simpleName.equals(PLUGIN_GOAL_SUFFIX)) {
      return null;
    }

    String stripped;
    if (simpleName.endsWith(PLUGIN_GOAL_SUFFIX)) {
      stripped = simpleName.substring(0, simpleName.length() - PLUGIN_GOAL_SUFFIX.length());
    } else {
      stripped = simpleName;
    }

    StringBuilder idBuilder = new StringBuilder();
    for (int i = 0; i < stripped.length(); i++) {
      char c = stripped.charAt(i);

      if (Character.isUpperCase(c)) {
        idBuilder.append(idBuilder.length() != 0 ? "-" : StringUtils.EMPTY);
        idBuilder.append(Character.toLowerCase(c));
      } else {
        idBuilder.append(c);
      }
    }

    return idBuilder.toString();
  }

  @Override
  public void execute(SitePluginGoal pluginGoal) {
    Objects.requireNonNull(pluginGoal, "plugin goal is null");

    execute(pluginGoal, Collections.emptyMap());
  }

  @Override
  public void execute(SitePluginGoal pluginGoal, Map<String, Object> properties) {
    Objects.requireNonNull(pluginGoal, "plugin goal is null");
    Objects.requireNonNull(properties, "properties are null");

    try {
      // inject properties
      site.getConf().inject(pluginGoal, properties);

      // execute goal
      pluginGoal.execute(site);
    } catch (Exception e) {
      throw new SitePluginException(format("Execution of plugin goal '%s' failed", pluginGoal.getClass().getName()), e);
    }
  }

  @Override
  public void execute(String pluginGoal) {
    Objects.requireNonNull(pluginGoal, "plugin goal is null");

    execute(pluginGoal, Collections.emptyMap());
  }

  @Override
  public void execute(String pluginGoal, Map<String, Object> properties) {
    Objects.requireNonNull(pluginGoal, "plugin goal is null");
    Objects.requireNonNull(properties, "properties are null");

    Class<? extends SitePluginGoal> pluginGoalType = pluginGoalTypeMap.get(pluginGoal);
    if (pluginGoalType == null) {
      // try to find plugin goal type by fully qualified name
      pluginGoalType = findPluginGoalType(pluginGoal);
    }

    SitePluginGoal instance;
    try {
      instance = pluginGoalType.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new SitePluginException(format("Plugin goal '%s' could not be instantiated''", pluginGoalType), e);
    }

    execute(instance, properties);
  }

  protected Class<? extends SitePluginGoal> findPluginGoalType(String pluginGoal) {
    Optional<Class<? extends SitePluginGoal>> pluginGoalType = pluginGoalTypes.stream()
        // filter type by name
        .filter(type -> type.getName().equals(pluginGoal))
        // find first
        .findFirst();

    if (pluginGoalType.isEmpty()) {
      throw new SitePluginException(format("Plugin goal '%s' could not be found", pluginGoal));
    }

    return pluginGoalType.get();
  }

  @Override
  public Set<SitePluginDesc> getPlugins() {
    return plugins.stream().map(this::buildPluginDesc).collect(Collectors.toSet());
  }

  @Override
  public SitePluginGoalDesc getPluginGoal(String pluginGoal) {
    return buildPluginGoalDesc(findPluginGoalType(pluginGoal));
  }

  @Override
  public void preBuild(SiteBuilder builder) {
    // call preBuild hook of each loaded plugin
    ServiceLoader.load(SitePlugin.class).forEach(plugin -> {
      addPlugin(plugin);

      plugin.preBuild(builder);
    });
  }

  @Override
  public void postBuild(Site site) {
    this.site = site;

    // inject and call postBuild hook at each plugin
    plugins.stream().forEach(plugin -> plugin.postBuild(site));

    Set<String> ambiguousNames = new HashSet<>();
    for (Class<? extends SitePluginGoal> pluginGoalType : pluginGoalTypes) {
      String id = buildPluginGoalId(pluginGoalType.getSimpleName());
      if (id == null) {
        continue;
      }

      boolean ambiguous = pluginGoalTypeMap.put(id, pluginGoalType) != null;
      if (ambiguous) {
        ambiguousNames.add(id);
      }
    }

    ambiguousNames.forEach(pluginGoalTypeMap::remove);
    ambiguousNames.clear();
  }

  @Override
  public void preDestroy(Site site) {
    try {
      plugins.forEach(plugin -> plugin.preDestroy(site));
    } catch (Exception e) {
      // ignore any exception
    }
  }
}
