package com.github.gclaussn.ssg.impl;

import java.util.Set;
import java.util.stream.Collectors;

import com.github.gclaussn.ssg.PageInclude;
import com.github.gclaussn.ssg.Source;

abstract class AbstractSource {

  protected final SiteImpl site;

  AbstractSource(SiteImpl site) {
    this.site = site;
  }

  protected abstract void init();

  protected abstract void destroy();

  protected Set<PageInclude> collectPageIncludes(Set<String> ids) {
    return ids.stream().filter(site::hasPageInclude).map(site::getPageInclude).collect(Collectors.toSet());
  }

  protected abstract boolean dependsOn(Source source);
}
