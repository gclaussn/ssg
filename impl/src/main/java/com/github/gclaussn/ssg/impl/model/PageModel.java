package com.github.gclaussn.ssg.impl.model;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

class PageModel {

  protected transient Path filePath;

  protected transient String markdown;

  protected Map<String, Object> data;
  protected List<PageDataSelectorBeanImpl> dataSelectors;
  protected Set<String> includes;
  @JsonProperty("out")
  protected String outputName;
  protected Boolean skip;
}
