package com.github.gclaussn.ssg.builtin.selector;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.SourceType;
import com.github.gclaussn.ssg.data.PageDataSelector;

public class PageSetAggregator implements PageDataSelector {

  protected String pageSetId;

  protected String distinct;

  private Site site;

  @Override
  public void init(Site site) {
    this.site = site;

    Objects.requireNonNull(pageSetId, "page set ID is null");
  }

  @Override
  public boolean dependsOn(Source source) {
    return source.getType() == SourceType.PAGE_SET && source.getId().equals(pageSetId);
  }

  @Override
  public Object select(Page page) {
    Set<Object> set = new TreeSet<>();

    site.getPageSet(pageSetId).getPages().stream()
        // filter not rejected pages
        .filter(p -> !p.isRejected())
        // get value from page data
        .map(p -> p.getData().get(distinct).as(Object.class))
        // filter not null values
        .filter(Objects::nonNull)
        // add value to set
        .forEach(set::add);

    return set;
  }
}
