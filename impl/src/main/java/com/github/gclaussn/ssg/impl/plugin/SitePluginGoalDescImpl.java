package com.github.gclaussn.ssg.impl.plugin;

import java.util.Set;

import com.github.gclaussn.ssg.conf.SitePropertyDesc;
import com.github.gclaussn.ssg.plugin.SitePluginGoalDesc;

class SitePluginGoalDescImpl implements SitePluginGoalDesc {

  protected String documentation;
  protected String id;
  protected String name;
  protected Set<SitePropertyDesc> properties;
  protected String typeName;

  @Override
  public int compareTo(SitePluginGoalDesc desc) {
    if (id != null) {
      return id.compareTo(desc.getId());
    } else if (desc.getId() != null) {
      return -1;
    } else {
      return typeName.compareTo(desc.getTypeName());
    }
  }

  @Override
  public String getDocumentation() {
    return documentation;
  }

  @Override
  public String getId() {
    return id;
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
}
