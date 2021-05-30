package com.github.gclaussn.ssg.impl.model;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.github.gclaussn.ssg.PageBuilder;
import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.data.PageData;

class PageBuilderImpl implements PageBuilder {

  private final SiteModelRepository repository;

  protected final String pageId;

  protected Map<String, Object> data;
  protected boolean skip;

  PageBuilderImpl(SiteModelRepository repository, String pageId) {
    this.repository = repository;
    this.pageId = pageId;
  }

  @Override
  public PageBuilder data(PageData data) {
    Objects.requireNonNull(data, "data is null");

    this.data = data.getRootMap();
    return this;
  }

  @Override
  public Optional<SiteError> save() {
    return repository.savePage(this);
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

  @Override
  public PageBuilder skip(boolean skip) {
    this.skip = skip;
    return this;
  }
}
