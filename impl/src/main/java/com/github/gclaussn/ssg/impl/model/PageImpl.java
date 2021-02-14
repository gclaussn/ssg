package com.github.gclaussn.ssg.impl.model;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.PageInclude;
import com.github.gclaussn.ssg.PageSet;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.SourceType;
import com.github.gclaussn.ssg.data.PageData;
import com.github.gclaussn.ssg.data.PageDataSelectorBean;
import com.github.gclaussn.ssg.error.SiteError;

class PageImpl extends AbstractSource implements Page {

  protected PageData data;
  protected List<PageDataSelectorBeanImpl> dataSelectors;
  protected Set<String> includes;
  protected Path modelPath;
  protected String outputName;
  protected String rejectedBy;
  protected boolean skip;
  protected String setId;
  protected String subId;
  protected String templateName;
  protected String url;

  PageImpl(Site site) {
    super(site);
  }

  @Override
  protected void init() {
    dataSelectors.forEach(AbstractBean::init);
  }

  @Override
  protected void destroy() {
    includes.clear();

    dataSelectors.forEach(AbstractBean::destroy);
    dataSelectors.clear();
  }

  @Override
  public boolean dependsOn(Source source) {
    return dataSelectors.stream().anyMatch(dataSelector -> dataSelector.dependsOn(source));
  }

  @Override
  public Optional<SiteError> generate() {
    return site.getGenerator().generatePage(id);
  }

  @Override
  public PageData getData() {
    return data;
  }

  @Override
  public List<PageDataSelectorBean> getDataSelectors() {
    return new LinkedList<>(dataSelectors);
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public Optional<Path> getModelPath() {
    return Optional.ofNullable(modelPath);
  }

  @Override
  public String getOutputName() {
    return outputName;
  }

  @Override
  public Path getOutputPath() {
    return site.getOutputPath().resolve(outputName);
  }

  @Override
  public Set<PageInclude> getPageIncludes() {
    return collectPageIncludes(includes);
  }

  @Override
  public Set<String> getPageIncludeIds() {
    return new HashSet<>(includes);
  }

  @Override
  public Optional<PageSet> getPageSet() {
    return setId != null ? Optional.ofNullable(site.getPageSet(setId)) : Optional.empty();
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
  public boolean isGenerated() {
    return !isSkipped() && !isRejected();
  }

  @Override
  public boolean isRejected() {
    return rejectedBy != null;
  }

  @Override
  public boolean isSkipped() {
    return skip;
  }
}
