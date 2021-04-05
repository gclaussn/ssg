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
import com.github.gclaussn.ssg.plugin.SitePluginAction;
import com.github.gclaussn.ssg.plugin.SitePluginActionDesc;
import com.github.gclaussn.ssg.plugin.SitePluginDesc;
import com.github.gclaussn.ssg.plugin.SitePluginException;
import com.github.gclaussn.ssg.plugin.SitePluginManager;

public class SitePluginManagerImpl implements SitePluginManager, SitePlugin {

  /** Common suffix of {@link SitePluginAction} implementations. */
  protected static final String PLUGIN_ACTION_SUFFIX = "Action";

  private final List<SitePluginHolder> plugins;

  private final Set<Class<? extends SitePluginAction>> pluginActionTypes;

  private final Map<String, Class<? extends SitePluginAction>> pluginActionTypeMap;

  private Site site;

  public SitePluginManagerImpl() {
    plugins = new ArrayList<>();
    pluginActionTypes = new HashSet<>();
    pluginActionTypeMap = new HashMap<>();

  }

  protected void addPlugin(SitePlugin plugin) {
    plugins.add(new SitePluginHolder(plugin));
  }

  public void addPluginAction(Class<? extends SitePluginAction> pluginActionType) {
    if (plugins.isEmpty()) {
      return;
    }

    plugins.get(plugins.size() - 1).addPluginAction(pluginActionType);
    pluginActionTypes.add(pluginActionType);
  }

  protected SitePluginDesc buildPluginDesc(SitePluginHolder holder) {
    Class<?> type = holder.getPlugin().getClass();

    TypeDesc typeDesc = site.getConfiguration().describe(type);

    SitePluginDescImpl desc = new SitePluginDescImpl();
    desc.documentation = typeDesc.getDocumentation();
    desc.name = typeDesc.getName();
    desc.properties.addAll(typeDesc.getProperties());
    desc.typeName = type.getName();

    holder.getPluginActionTypes().stream().map(Class::getName).forEach(desc.actions::add);

    return desc;
  }

  protected SitePluginActionDesc buildPluginActionDesc(Class<? extends SitePluginAction> pluginActionType) {
    TypeDesc typeDesc = site.getConfiguration().describe(pluginActionType);

    SitePluginActionDescImpl desc = new SitePluginActionDescImpl();
    desc.documentation = typeDesc.getDocumentation();
    desc.name = typeDesc.getName();
    desc.id = buildPluginActionId(pluginActionType.getSimpleName());
    desc.properties = typeDesc.getProperties();
    desc.typeName = pluginActionType.getName();

    return desc;
  }

  protected String buildPluginActionId(String simpleName) {
    if (simpleName.equals(PLUGIN_ACTION_SUFFIX)) {
      return null;
    }

    String stripped;
    if (simpleName.endsWith(PLUGIN_ACTION_SUFFIX)) {
      stripped = simpleName.substring(0, simpleName.length() - PLUGIN_ACTION_SUFFIX.length());
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
  public void execute(SitePluginAction pluginAction) {
    Objects.requireNonNull(pluginAction, "plugin action is null");

    execute(pluginAction, Collections.emptyMap());
  }

  @Override
  public void execute(SitePluginAction pluginAction, Map<String, Object> properties) {
    Objects.requireNonNull(pluginAction, "plugin action is null");
    Objects.requireNonNull(properties, "properties are null");

    try {
      // inject properties
      site.getConfiguration().inject(pluginAction, properties);

      // execute action
      pluginAction.execute(site);
    } catch (SitePluginException e) {
      // rethrow managed exception
      throw e;
    } catch (Exception e) {
      String message = format("Execution of plugin action '%s' failed", pluginAction.getClass().getName());
      throw new SitePluginException(message, e);
    }
  }

  @Override
  public void execute(String pluginAction) {
    Objects.requireNonNull(pluginAction, "plugin action is null");

    execute(pluginAction, Collections.emptyMap());
  }

  @Override
  public void execute(String pluginAction, Map<String, Object> properties) {
    Objects.requireNonNull(pluginAction, "plugin action is null");
    Objects.requireNonNull(properties, "properties are null");

    Class<? extends SitePluginAction> pluginActionType = pluginActionTypeMap.get(pluginAction);
    if (pluginActionType == null) {
      // try to find plugin action type by fully qualified name
      pluginActionType = findPluginActionType(pluginAction);
    }

    SitePluginAction instance;
    try {
      instance = pluginActionType.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new SitePluginException(format("Plugin action '%s' could not be instantiated''", pluginActionType), e);
    }

    execute(instance, properties);
  }

  protected Class<? extends SitePluginAction> findPluginActionType(String pluginAction) {
    Optional<Class<? extends SitePluginAction>> pluginActionType = pluginActionTypes.stream()
        // filter type by name
        .filter(type -> type.getName().equals(pluginAction))
        // find first
        .findFirst();

    if (pluginActionType.isEmpty()) {
      throw new SitePluginException(format("Plugin action '%s' could not be found", pluginAction));
    }

    return pluginActionType.get();
  }

  @Override
  public Set<SitePluginDesc> getPlugins() {
    return plugins.stream().map(this::buildPluginDesc).collect(Collectors.toSet());
  }

  @Override
  public SitePluginActionDesc getPluginAction(String pluginAction) {
    return buildPluginActionDesc(findPluginActionType(pluginAction));
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
    for (Class<? extends SitePluginAction> pluginActionType : pluginActionTypes) {
      String id = buildPluginActionId(pluginActionType.getSimpleName());
      if (id == null) {
        continue;
      }

      boolean ambiguous = pluginActionTypeMap.put(id, pluginActionType) != null;
      if (ambiguous) {
        ambiguousNames.add(id);
      }
    }

    ambiguousNames.forEach(pluginActionTypeMap::remove);
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
