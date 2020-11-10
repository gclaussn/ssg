package com.github.gclaussn.ssg;

public interface PageProcessor {

  default void init(Site site) {
    // empty default implementation
  }

  Object process(Page page);

  default void destroy() {
    // empty default implementation
  }
}
