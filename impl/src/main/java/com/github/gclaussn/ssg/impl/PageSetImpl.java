package com.github.gclaussn.ssg.impl;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.PageInclude;
import com.github.gclaussn.ssg.PageSet;
import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.SourceType;
import com.github.gclaussn.ssg.data.PageData;

class PageSetImpl extends AbstractSource implements PageSet {

  protected final PageSetModel model;

  protected String id;
  protected Set<String> pages;
  protected String templateName;

  PageSetImpl(SiteImpl site, PageSetModel model) {
    super(site);

    this.model = model;
  }

  @Override
  protected void init() {
    for (PageDataSelectorBeanImpl bean : model.dataSelectors) {
      bean.init(site, this);
    }

    for (PageFilterBeanImpl bean : model.filters) {
      bean.init(site, this);
    }

    for (PageProcessorBeanImpl bean : model.processors) {
      bean.init(site, this);
    }
  }

  @Override
  protected void destroy() {
    pages.clear();

    model.includes.clear();

    model.dataSelectors.forEach(AbstractBean::destroy);
    model.dataSelectors.clear();
    model.filters.forEach(AbstractBean::destroy);
    model.filters.clear();
    model.processors.forEach(AbstractBean::destroy);
    model.processors.clear();
  }

  @Override
  protected boolean dependsOn(Source source) {
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
    if (!(obj instanceof PageSet)) {
      return false;
    }

    PageSet pageSet = (PageSet) obj;
    return id.equals(pageSet.getId());
  }

  @Override
  public List<SiteError> generate() {
    return site.getGenerator().generatePageSet(id);
  }

  @Override
  public Optional<String> getBasePath() {
    return Optional.ofNullable(model.basePath);
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
  public Set<Page> getPages() {
    return pages.stream()
        // only loaded pages
        .filter(site::hasPage)
        // get page
        .map(site::getPage)
        // collect ordered
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  @Override
  public Set<PageInclude> getPageIncludes() {
    return collectPageIncludes(model.includes);
  }

  @Override
  public String getTemplateName() {
    return templateName;
  }

  @Override
  public Path getTemplatePath() {
    return site.getSourcePath().resolve(templateName);
  }

  @Override
  public SourceType getType() {
    return SourceType.PAGE_SET;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public boolean isSkipped() {
    return model.skip != null && model.skip.booleanValue();
  }

  @Override
  public String toString() {
    return String.format("%s[id=%s]", getType(), id);
  }
}
