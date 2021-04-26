package com.github.gclaussn.ssg.impl.model;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.PageInclude;
import com.github.gclaussn.ssg.PageSet;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.SourceType;
import com.github.gclaussn.ssg.data.PageData;
import com.github.gclaussn.ssg.data.PageDataSelectorBean;

class PageSetImpl extends AbstractSource implements PageSet {

  protected String basePath;
  protected PageData data;
  protected List<PageDataSelectorBeanImpl> dataSelectors;
  protected List<PageFilterBeanImpl> filters;
  protected Set<String> includes;
  protected Path modelPath;
  protected Set<String> pages;
  protected List<PageProcessorBeanImpl> processors;
  protected boolean skip;
  protected String templateName;

  PageSetImpl(Site site) {
    super(site);
  }

  @Override
  protected void init() {
    dataSelectors.forEach(AbstractBean::init);
    filters.forEach(AbstractBean::init);
    processors.forEach(AbstractBean::init);
  }

  @Override
  protected void destroy() {
    pages.clear();

    includes.clear();

    dataSelectors.forEach(AbstractBean::destroy);
    dataSelectors.clear();
    filters.forEach(AbstractBean::destroy);
    filters.clear();
    processors.forEach(AbstractBean::destroy);
    processors.clear();
  }

  @Override
  public List<SiteError> generate() {
    return site.getGenerator().generatePageSet(id);
  }

  @Override
  public Optional<String> getBasePath() {
    return Optional.ofNullable(basePath);
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
  public Optional<Path> getModelPath() {
    return Optional.ofNullable(modelPath);
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public Set<Page> getPages() {
    return collectPages(pages);
  }

  @Override
  public Set<PageInclude> getPageIncludes() {
    return collectPageIncludes(includes);
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
  public boolean isSkipped() {
    return skip;
  }
}
