package com.github.gclaussn.ssg;

public interface PageProcessorBean {

  String getId();

  Object process(Page page);
}
