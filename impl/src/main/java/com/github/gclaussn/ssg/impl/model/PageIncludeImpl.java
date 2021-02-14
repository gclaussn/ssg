package com.github.gclaussn.ssg.impl.model;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

import com.github.gclaussn.ssg.PageInclude;
import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.SourceType;
import com.github.gclaussn.ssg.data.PageData;

class PageIncludeImpl extends AbstractSource implements PageInclude {

  protected PageData data;
  protected Set<String> includes;
  protected Path modelPath;
  protected String templateName;

  private final SiteModelRepository repository;

  PageIncludeImpl(SiteModelRepository repository) {
    super(repository.site);

    this.repository = repository;
  }

  @Override
  protected void destroy() {
    includes.clear();
  }

  @Override
  public boolean dependsOn(Source source) {
    if (includes.contains(source.getId())) {
      return true;
    }

    for (String pageIncludeId : includes) {
      PageIncludeImpl pageInclude = repository.pageIncludes.get(pageIncludeId);
      if (pageInclude != null && pageInclude.dependsOn(source)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public PageData getData() {
    return data;
  }

  @Override
  public Optional<Path> getModelPath() {
    return Optional.ofNullable(modelPath);
  }

  @Override
  public Set<PageInclude> getPageIncludes() {
    return collectPageIncludes(includes);
  }

  @Override
  public Optional<String> getTemplateName() {
    return Optional.ofNullable(templateName);
  }

  @Override
  public Optional<Path> getTemplatePath() {
    return templateName != null ? Optional.of(site.getSourcePath().resolve(templateName)) : Optional.empty();
  }

  @Override
  public SourceType getType() {
    return SourceType.PAGE_INCLUDE;
  }
}
