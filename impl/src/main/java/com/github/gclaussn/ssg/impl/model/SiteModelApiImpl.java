package com.github.gclaussn.ssg.impl.model;

import java.util.Objects;

import com.github.gclaussn.ssg.PageBuilder;
import com.github.gclaussn.ssg.SiteModelApi;

class SiteModelApiImpl implements SiteModelApi {

  private final SiteModelRepository repository;

  SiteModelApiImpl(SiteModelRepository repository) {
    this.repository = repository;
  }

  @Override
  public PageBuilder createPageBuilder(String pageId) {
    Objects.requireNonNull(pageId, "page ID is null");

    if (pageId.isBlank()) {
      throw new IllegalArgumentException("page ID is empty or blank");
    }

    return new PageBuilderImpl(repository, pageId);
  }
}
