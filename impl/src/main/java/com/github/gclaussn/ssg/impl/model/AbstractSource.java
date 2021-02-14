package com.github.gclaussn.ssg.impl.model;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.PageInclude;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.Source;

abstract class AbstractSource implements Source {

  protected final Site site;

  protected String id;

  AbstractSource(Site site) {
    this.site = site;
  }

  protected Set<Page> collectPages(Set<String> ids) {
    return ids.stream().filter(site::hasPage).map(site::getPage).collect(Collectors.toCollection(LinkedHashSet::new));
  }

  protected Set<PageInclude> collectPageIncludes(Set<String> ids) {
    return ids.stream().filter(site::hasPageInclude).map(site::getPageInclude).collect(Collectors.toSet());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof Source)) {
      return false;
    }

    Source source = (Source) obj;
    return getType() == source.getType() && id.equals(source.getId());
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  protected void init() {
    // empty default implementation
  }

  protected void destroy() {
    // empty default implementation
  }

  @Override
  public String toString() {
    return String.format("%s[id=%s]", getType(), id);
  }
}
