package com.github.gclaussn.ssg;

import java.util.Optional;

import com.github.gclaussn.ssg.data.PageData;

public interface PageBuilder {

  PageBuilder addPageInclude(String pageIncludeId);

  PageBuilder data(PageData data);

  PageBuilder markdown(String markdown);

  PageBuilder outputName(String outputName);

  PageBuilder skip(boolean skip);

  Optional<SiteError> save();

  Optional<SiteError> saveAndLoad();
}
