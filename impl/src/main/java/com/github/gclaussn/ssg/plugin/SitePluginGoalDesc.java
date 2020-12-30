package com.github.gclaussn.ssg.plugin;

import java.util.Set;

import com.github.gclaussn.ssg.conf.SitePropertyDesc;

public interface SitePluginGoalDesc extends Comparable<SitePluginGoalDesc> {

  String getDocumentation();

  String getId();

  String getName();

  Set<SitePropertyDesc> getProperties();

  String getTypeName();
}
