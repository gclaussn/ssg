package com.github.gclaussn.ssg.impl.plugin;

import java.util.Set;
import java.util.TreeSet;

import com.github.gclaussn.ssg.conf.SitePropertyDesc;
import com.github.gclaussn.ssg.plugin.SitePluginDesc;

class SitePluginDescImpl implements SitePluginDesc {

  protected final Set<String> actions;
  protected final Set<SitePropertyDesc> properties;

  protected String documentation;
  protected String name;
  protected String typeName;

  SitePluginDescImpl() {
    actions = new TreeSet<>();
    properties = new TreeSet<>();
  }

  @Override
  public int compareTo(SitePluginDesc desc) {
    return typeName.compareTo(desc.getTypeName());
  }

  @Override
  public Set<String> getActions() {
    return actions;
  }

  @Override
  public String getDocumentation() {
    return documentation;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Set<SitePropertyDesc> getProperties() {
    return properties;
  }

  @Override
  public String getTypeName() {
    return typeName;
  }

  @Override
  public int hashCode() {
    return typeName.hashCode();
  }
}
