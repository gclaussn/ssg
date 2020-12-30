package com.github.gclaussn.ssg.plugin;

import java.util.Set;

import com.github.gclaussn.ssg.conf.SitePropertyDesc;

public interface SitePluginDesc extends Comparable<SitePluginDesc> {

  String getDocumentation();

  Set<String> getGoals();

  String getName();

  Set<SitePropertyDesc> getProperties();

  String getTypeName();
}
