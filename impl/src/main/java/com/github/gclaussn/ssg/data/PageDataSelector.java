package com.github.gclaussn.ssg.data;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.Source;

public interface PageDataSelector {

  default boolean dependsOn(Source source) {
    return false;
  }

  default void init(Site site) {
    // empty default implementation
  }

  Object select(Page page);

  default void destroy() {
    // empty default implementation
  }
}
