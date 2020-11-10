package com.github.gclaussn.ssg.plugin;

import java.util.Collections;
import java.util.List;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteBuilder;

public interface SitePlugin {

  default List<SitePluginGoal> getGoals() {
    return Collections.emptyList();
  }
  
  default void preBuild(SiteBuilder builder) {
    // empty default implementation
  }

  default void preDestroy(Site site) {
    // empty default implementation
  }

  default void postBuild(Site site) {
    // empty default implementation
  }
}
