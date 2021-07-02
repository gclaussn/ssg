package com.github.gclaussn.ssg.impl.plugin;

import java.util.List;

import com.github.gclaussn.ssg.conf.SitePropertyDesc;
import com.github.gclaussn.ssg.plugin.SitePluginActionDesc;

class SitePluginActionDescImpl implements SitePluginActionDesc {

  protected String documentation;
  protected String id;
  protected String name;
  protected List<SitePropertyDesc> properties;
  protected String typeName;

  @Override
  public int compareTo(SitePluginActionDesc desc) {
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
  public List<SitePropertyDesc> getProperties() {
    return properties;
  }

  @Override
  public String getTypeName() {
    return typeName;
  }
}
