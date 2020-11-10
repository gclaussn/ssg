package com.github.gclaussn.ssg.impl;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

import com.github.gclaussn.ssg.PageInclude;
import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.SourceType;
import com.github.gclaussn.ssg.data.PageData;

class PageIncludeImpl extends AbstractSource implements PageInclude {

  protected final PageIncludeModel model;

  protected String id;
  protected String templateName;

  PageIncludeImpl(SiteImpl site, PageIncludeModel model) {
    super(site);

    this.model = model;
  }

  @Override
  protected void init() {
    // nothing to do here
  }

  @Override
  protected void destroy() {
    model.includes.clear();
  }

  @Override
  protected boolean dependsOn(Source source) {
    if (model.includes.contains(source.getId())) {
      return true;
    }

    for (String id : model.includes) {
      PageIncludeImpl pageInclude = site.pageIncludes.get(id);
      if (pageInclude == null) {
        continue;
      }
      if (pageInclude.dependsOn(source)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof PageInclude)) {
      return false;
    }

    PageInclude pageInclude = (PageInclude) obj;
    return id.equals(pageInclude.getId());
  }

  @Override
  public PageData getData() {
    return model.data;
  }

  @Override
  public Optional<Path> getModelPath() {
    return Optional.ofNullable(model.filePath);
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public Set<PageInclude> getPageIncludes() {
    return collectPageIncludes(model.includes);
  }

  @Override
  public Optional<String> getTemplateName() {
    return Optional.ofNullable(templateName);
  }

  @Override
  public Optional<Path> getTemplatePath() {
    if (templateName != null) {
      return Optional.of(site.getSourcePath().resolve(templateName));
    } else {
      return Optional.empty();
    }
  }

  @Override
  public SourceType getType() {
    return SourceType.PAGE_INCLUDE;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return String.format("%s[id=%s]", getType(), id);
  }
}
