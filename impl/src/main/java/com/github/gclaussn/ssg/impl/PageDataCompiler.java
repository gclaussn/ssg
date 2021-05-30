package com.github.gclaussn.ssg.impl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.PageInclude;
import com.github.gclaussn.ssg.data.PageData;
import com.github.gclaussn.ssg.data.PageDataBuilder;
import com.github.gclaussn.ssg.data.PageDataSelectorBean;

/**
 * @see HashCodeBasedPageDataCompiler
 */
class PageDataCompiler implements Function<Page, PageData> {

  @Override
  public PageData apply(Page page) {
    PageDataBuilder builder = PageData.builder();

    // put page data
    builder.putRoot(page.getData());

    // put data from page includes
    for (PageInclude pageInclude : resolvePageIncludes(page)) {
      String id = normalizeId(pageInclude.getId());
      builder.put(id, pageInclude.getData().getRootMap());
    }

    // put data from selectors
    for (PageDataSelectorBean dataSelector : page.getDataSelectors()) {
      String id = normalizeId(dataSelector.getId());
      builder.putIfAbsent(id, dataSelector.select(page));
    }

    return builder.build();
  }

  protected String normalizeId(String id) {
    StringBuilder sb = new StringBuilder(id.length());

    boolean toUpper = false;
    for (int i = 0; i < id.length(); i++) {
      char c = id.charAt(i);

      if (c == '-' || c == '_') {
        toUpper = true;
      } else if (toUpper) {
        sb.append(Character.toUpperCase(c));
        toUpper = false;
      } else {
        sb.append(c);
      }
    }

    return sb.toString();
  }

  protected Set<PageInclude> resolvePageIncludes(Page page) {
    Queue<PageInclude> queue = new LinkedList<>();
    for (PageInclude pageInclude : page.getPageIncludes()) {
      queue.add(pageInclude);
    }

    Set<PageInclude> pageIncludes = new HashSet<>();
    while (!queue.isEmpty()) {
      PageInclude pageInclude = queue.poll();
      if (pageIncludes.contains(pageInclude)) {
        continue;
      }

      pageIncludes.add(pageInclude);
      pageInclude.getPageIncludes().stream().forEach(queue::add);
    }

    return pageIncludes;
  }
}
