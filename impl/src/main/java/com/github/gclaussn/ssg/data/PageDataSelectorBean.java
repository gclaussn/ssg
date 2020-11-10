package com.github.gclaussn.ssg.data;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.Source;

public interface PageDataSelectorBean {

  boolean dependsOn(Source source);

  String getId();

  Object select(Page page);
}
