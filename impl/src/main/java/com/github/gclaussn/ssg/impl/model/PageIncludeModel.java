package com.github.gclaussn.ssg.impl.model;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

class PageIncludeModel {

  protected transient Path filePath;

  protected Map<String, Object> data;
  protected Set<String> includes;
}
