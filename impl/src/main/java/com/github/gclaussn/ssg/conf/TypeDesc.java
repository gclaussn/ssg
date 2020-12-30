package com.github.gclaussn.ssg.conf;

import java.util.Set;

import com.github.gclaussn.ssg.plugin.SitePlugin;
import com.github.gclaussn.ssg.plugin.SitePluginGoal;

/**
 * Description of a site type.<br />
 * Types are:
 * <ul>
 *   <li>{@link SitePlugin}</li>
 *   <li>{@link SitePluginGoal}</li>
 * </ul 
 */
public interface TypeDesc {

  String getDocumentation();

  String getName();

  Set<SitePropertyDesc> getProperties();
}
