package com.github.gclaussn.ssg.impl;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.PageInclude;
import com.github.gclaussn.ssg.PageSet;
import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.SourceType;
import com.github.gclaussn.ssg.data.PageData;
import com.github.gclaussn.ssg.data.PageDataSelectorBean;

class PageImpl extends AbstractSource implements Page {

  protected final PageModel model;

  protected String id;
  protected String rejectedBy;
  protected String setId;
  protected String subId;
  protected String templateName;
  protected String url;

  PageImpl(SiteImpl site, PageModel model) {
    super(site);

    this.model = model;
  }

  @Override
  protected void init() {
    for (PageDataSelectorBeanImpl bean : model.dataSelectors) {
      bean.init(site, this);
    }
  }

  @Override
  protected void destroy() {
    model.includes.clear();

    model.dataSelectors.forEach(AbstractBean::destroy);
    model.dataSelectors.clear();
  }

  @Override
  protected boolean dependsOn(Source source) {
    for (PageDataSelectorBean dataSelector : model.dataSelectors) {
      if (dataSelector.dependsOn(source)) {
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
    if (!(obj instanceof Page)) {
      return false;
    }

    Page page = (Page) obj;
    return id.equals(page.getId());
  }

  @Override
  public Optional<SiteError> generate() {
    return site.getGenerator().generatePage(id);
  }

  @Override
  public PageData getData() {
    return model.data;
  }

  @Override
  public List<PageDataSelectorBean> getDataSelectors() {
    return new LinkedList<>(model.dataSelectors);
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public Optional<Path> getModelPath() {
    return Optional.ofNullable(model.filePath);
  }

  @Override
  public String getOutputName() {
    return model.outputName;
  }

  @Override
  public Path getOutputPath() {
    return site.getOutputPath().resolve(model.outputName);
  }

  @Override
  public Set<PageInclude> getPageIncludes() {
    return collectPageIncludes(model.includes);
  }

  @Override
  public Optional<PageSet> getPageSet() {
    if (setId != null) {
      return Optional.ofNullable(site.pageSets.get(setId));
    } else {
      return Optional.empty();
    }
  }

  @Override
  public Optional<String> getRejectedBy() {
    return Optional.ofNullable(rejectedBy);
  }

  @Override
  public Optional<String> getSubId() {
    return Optional.ofNullable(subId);
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
    return SourceType.PAGE;
  }

  @Override
  public String getUrl() {
    return url;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public boolean isRejected() {
    return rejectedBy != null;
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
