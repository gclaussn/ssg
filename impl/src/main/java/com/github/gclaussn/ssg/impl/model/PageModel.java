package com.github.gclaussn.ssg.impl.model;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

class PageModel {

  protected transient Path filePath;

  protected transient String markdown;

  protected Map<String, Object> data;
  @JsonDeserialize(as = LinkedHashMap.class)
  protected Map<String, PageDataSelectorBeanImpl> dataSelectors;
  protected Set<String> includes;
  @JsonProperty("out")
  protected String outputName;
  protected Boolean skip;
}
