package com.github.gclaussn.ssg.plugin;

import java.util.Set;

import com.github.gclaussn.ssg.conf.TypeDesc;

public interface SitePluginDesc extends TypeDesc, Comparable<SitePluginDesc> {

  Set<String> getActions();

  String getTypeName();
}
