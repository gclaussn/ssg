package com.github.gclaussn.ssg.builtin.selector;

import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.SourceType;
import com.github.gclaussn.ssg.data.PageDataSelector;

public class PageSetAggregator implements PageDataSelector {

  protected String pageSetId;

  /** Location of the distinct values within the page data. */
  protected String distinct;

  private transient Site site;

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
    return site.getPageSet(pageSetId).getPages().stream()
        // filter not rejected pages
        .filter(p -> !p.isRejected())
        // get value from page data
        .map(p -> p.getData().get(distinct).as(Object.class))
        // filter not null values
        .filter(Objects::nonNull)
        // collect in sorted set
        .collect(Collectors.toCollection(TreeSet::new));
  }
}
