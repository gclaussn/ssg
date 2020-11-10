package com.github.gclaussn.ssg;

public interface PageFilterBean {

  String getId();

  boolean filter(Page page);
}
