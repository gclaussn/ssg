package com.github.gclaussn.ssg.impl.model;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

class PageSetModel {

  protected transient Path filePath;

  @JsonProperty("base")
  protected String basePath;
  protected Map<String, Object> data;
  @JsonDeserialize(as = LinkedHashMap.class)
  protected Map<String, PageDataSelectorBeanImpl> dataSelectors;
  @JsonDeserialize(as = LinkedHashMap.class)
  protected Map<String, PageFilterBeanImpl> filters;
  protected Set<String> includes;
  @JsonDeserialize(as = LinkedHashMap.class)
  protected Map<String, PageProcessorBeanImpl> processors;
  protected Boolean skip;
}
