package com.github.gclaussn.ssg.impl.model;

import static com.github.gclaussn.ssg.file.SiteFileType.YAML;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

import com.github.gclaussn.ssg.PageBuilder;
import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.data.PageData;

class PageBuilderImpl implements PageBuilder {

  private final SiteModelRepository repository;

  private final String pageId;

  private final PageModel model;

  PageBuilderImpl(SiteModelRepository repository, String pageId) {
    this.repository = repository;
    this.pageId = pageId;

    model = new PageModel();
    model.filePath = repository.site.getSourcePath().resolve(YAML.appendTo(pageId));
    model.includes = new HashSet<>();
  }

  @Override
  public PageBuilder addPageInclude(String pageIncludeId) {
    Objects.requireNonNull(pageIncludeId, "page include ID is null");

    model.includes.add(pageIncludeId);
    return this;
  }

  @Override
  public PageBuilder data(PageData data) {
    Objects.requireNonNull(data, "data is null");

    model.data = data.getRootMap();
    return this;
  }

  @Override
  public PageBuilder markdown(String markdown) {
    model.markdown = markdown;
    return this;
  }

  @Override
  public PageBuilder outputName(String outputName) {
    model.outputName = outputName;
    return this;
  }

  @Override
  public PageBuilder skip(boolean skip) {
    model.skip = skip ? Boolean.TRUE : null;
    return this;
  }

  @Override
  public Optional<SiteError> save() {
    return repository.savePage(pageId, model);
  }

  @Override
  public Optional<SiteError> saveAndLoad() {
    Optional<SiteError> error = save();
    if (error.isPresent()) {
      return error;
    }

    Optional<String> pageSetId = repository.findPageSetId(pageId);
    if (pageSetId.isPresent()) {
      return repository.loadPage(pageId, pageSetId.get());
    } else {
      return repository.loadPage(pageId);
    }
  }
}
