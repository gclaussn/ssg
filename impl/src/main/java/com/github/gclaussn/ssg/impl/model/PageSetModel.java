package com.github.gclaussn.ssg.impl.model;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

class PageSetModel {

  protected transient Path filePath;

  @JsonProperty("base")
  protected String basePath;
  protected Map<String, Object> data;
  protected List<PageDataSelectorBeanImpl> dataSelectors;
  protected List<PageFilterBeanImpl> filters;
  protected Set<String> includes;
  protected List<PageProcessorBeanImpl> processors;
  protected Boolean skip;
}
