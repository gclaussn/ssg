package com.github.gclaussn.ssg.impl;

import java.nio.file.Path;
import java.util.Set;

import com.github.gclaussn.ssg.data.PageData;

class PageIncludeModel {

  protected transient Path filePath;

  protected PageData data;
  protected Set<String> includes;
}
