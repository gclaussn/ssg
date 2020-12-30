package com.github.gclaussn.ssg.plugin;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteBuilder;

public interface SitePlugin {
  
  default void preBuild(SiteBuilder builder) {
    // empty default implementation
  }

  default void postBuild(Site site) {
    // empty default implementation
  }

  default void preDestroy(Site site) {
    // empty default implementation
  }
}
