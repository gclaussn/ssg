package com.github.gclaussn.ssg.server.domain;

import java.nio.file.Path;

import com.github.gclaussn.ssg.PageSet;

public class PageSetDTO {

  public static PageSetDTO of(PageSet pageSet) {
    PageSetDTO target = new PageSetDTO();
    target.basePath = pageSet.getBasePath().orElse(null);
    target.id = pageSet.getId();
    target.modelPath = pageSet.getModelPath().orElse(null);
    target.skipped = pageSet.isSkipped();
    target.templateName = pageSet.getTemplateName();
    target.templatePath = pageSet.getTemplatePath();

    return target;
  }

  private String basePath;
  private String id;
  private Path modelPath;
  private boolean skipped;
  private String templateName;
  private Path templatePath;

  public String getBasePath() {
    return basePath;
  }

  public String getId() {
    return id;
  }

  public Path getModelPath() {
    return modelPath;
  }

  public String getTemplateName() {
    return templateName;
  }

  public Path getTemplatePath() {
    return templatePath;
  }

  public boolean isSkipped() {
    return skipped;
  }
}
