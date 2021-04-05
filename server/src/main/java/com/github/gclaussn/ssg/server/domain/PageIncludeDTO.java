package com.github.gclaussn.ssg.server.domain;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.gclaussn.ssg.PageInclude;

public class PageIncludeDTO {

  public static PageIncludeDTO of(PageInclude pageInclude) {
    PageIncludeDTO target = new PageIncludeDTO();
    target.data = pageInclude.getData().getRootMap();
    target.id = pageInclude.getId();
    target.modelPath = pageInclude.getModelPath().orElse(null);
    target.pageIncludes = pageInclude.getPageIncludes().stream().map(PageInclude::getId).collect(Collectors.toSet());

    return target;
  }

  private Map<String, Object> data;
  private String id;
  private Path modelPath;
  private Set<String> pageIncludes;

  public Map<String, Object> getData() {
    return data;
  }

  public String getId() {
    return id;
  }

  public Path getModelPath() {
    return modelPath;
  }

  public Set<String> getPageIncludes() {
    return pageIncludes;
  }
}
