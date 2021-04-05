package com.github.gclaussn.ssg.plugin;

import com.github.gclaussn.ssg.conf.TypeDesc;

public interface SitePluginActionDesc extends TypeDesc, Comparable<SitePluginActionDesc> {

  String getId();

  String getTypeName();
}
