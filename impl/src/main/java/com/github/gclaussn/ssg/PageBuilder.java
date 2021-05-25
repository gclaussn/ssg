package com.github.gclaussn.ssg;

import java.util.Optional;

import com.github.gclaussn.ssg.data.PageData;

public interface PageBuilder {

  PageBuilder data(PageData data);

  Optional<SiteError> save();

  Optional<SiteError> saveAndLoad();
}
