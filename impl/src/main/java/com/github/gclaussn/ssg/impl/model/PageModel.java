package com.github.gclaussn.ssg.impl.model;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.gclaussn.ssg.data.PageData;

class PageModel {

  protected transient Path filePath;

  protected PageData data;
  protected List<PageDataSelectorBeanImpl> dataSelectors;
  protected Set<String> includes;
  @JsonProperty("out")
  protected String outputName;
  protected Boolean skip;
}
