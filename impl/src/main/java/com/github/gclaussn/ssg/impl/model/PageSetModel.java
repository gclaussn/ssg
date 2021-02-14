package com.github.gclaussn.ssg.impl.model;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.gclaussn.ssg.data.PageData;

class PageSetModel {

  protected transient Path filePath;

  @JsonProperty("base")
  protected String basePath;
  protected PageData data;
  protected List<PageDataSelectorBeanImpl> dataSelectors;
  protected List<PageFilterBeanImpl> filters;
  protected Set<String> includes;
  protected List<PageProcessorBeanImpl> processors;
  protected Boolean skip;
}
