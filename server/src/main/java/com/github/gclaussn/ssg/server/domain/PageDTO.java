package com.github.gclaussn.ssg.server.domain;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.PageInclude;

public class PageDTO {

  public static PageDTO of(Page page) {
    PageDTO target = new PageDTO();
    target.id = page.getId();
    target.modelPath = page.getModelPath().orElse(null);
    target.outputName = page.getOutputName();
    target.outputPath = page.getOutputPath();
    target.rejected = page.isRejected();
    target.skipped = page.isSkipped();
    target.url = page.getUrl();

    // Ids of specified page includes
    target.pageIncludes = page.getPageIncludes().stream()
        .map(PageInclude::getId)
        .collect(Collectors.toCollection(LinkedHashSet::new));

    return target;
  }

  private String id;
  private Path modelPath;
  private String outputName;
  private Path outputPath;
  private Set<String> pageIncludes;
  private boolean rejected;
  private boolean skipped;
  private String url;

  public String getId() {
    return id;
  }

  public Path getModelPath() {
    return modelPath;
  }

  public String getOutputName() {
    return outputName;
  }

  public Path getOutputPath() {
    return outputPath;
  }

  public Set<String> getPageIncludes() {
    return pageIncludes;
  }

  public boolean isRejected() {
    return rejected;
  }

  public boolean isSkipped() {
    return skipped;
  }

  public String getUrl() {
    return url;
  }
}
