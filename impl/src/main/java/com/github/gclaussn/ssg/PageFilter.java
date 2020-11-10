package com.github.gclaussn.ssg;

public interface PageFilter {

  default void init(Site site) {
    // empty default implementation
  }

  boolean filter(Page page);

  default void destroy() {
    // empty default implementation
  }
}
