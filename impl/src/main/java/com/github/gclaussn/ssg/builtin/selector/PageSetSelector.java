package com.github.gclaussn.ssg.builtin.selector;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.SourceType;
import com.github.gclaussn.ssg.data.PageDataSelector;

public class PageSetSelector implements PageDataSelector {

  protected String pageSetId;

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
    List<Object> list = new LinkedList<>();

    site.getPageSet(pageSetId).getPages().stream()
        // filter not rejected pages
        .filter(p -> !p.isRejected())
        // get root map from page data
        .map(p -> p.getData().getRootMap())
        // add data to list
        .forEach(list::add);

    return list;
  }
}
