package com.github.gclaussn.ssg.conf;

import java.util.List;

import com.github.gclaussn.ssg.plugin.SitePlugin;
import com.github.gclaussn.ssg.plugin.SitePluginAction;

/**
 * Description of a type.<br>
 * 
 * Types are:
 * <ul>
 *   <li>{@link SitePlugin}</li>
 *   <li>{@link SitePluginAction}</li>
 * </ul 
 */
public interface TypeDesc {

  String getDocumentation();

  String getName();

  List<SitePropertyDesc> getProperties();
}
