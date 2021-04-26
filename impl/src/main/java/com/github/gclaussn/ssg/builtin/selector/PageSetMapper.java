package com.github.gclaussn.ssg.builtin.selector;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.SourceType;
import com.github.gclaussn.ssg.data.PageDataSelector;

public class PageSetMapper implements PageDataSelector {

  protected String pageSetId;

  protected String key;

  protected Set<String> excludes;
  protected Set<String> includes;

  private transient Site site;

  @Override
  public void init(Site site) {
    this.site = site;

    Objects.requireNonNull(pageSetId, "page set ID is null");
    Objects.requireNonNull(key, "key is null");
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
        // get root map from page data
        .map(p -> p.getData().getRootMap())
        // collect in map
        .collect(Collectors.toMap(this::mapKey, this::mapValue));
  }

  protected Object mapKey(Map<String, Object> data) {
    return data.get(key);
  }

  protected Object mapValue(Map<String, Object> data) {
    if (excludes != null) {
      data.keySet().removeAll(excludes);
    }
    if (includes != null) {
      data.keySet().removeIf(key -> !includes.contains(key));
    }

    return data;
  }
}
