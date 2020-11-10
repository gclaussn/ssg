package com.github.gclaussn.ssg.builtin;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.PageFilter;
import com.github.gclaussn.ssg.data.PageDataNode;

public class PublishedFilter implements PageFilter {

  protected static final String PUBLISHED = "published";

  @Override
  public boolean filter(Page page) {
    PageDataNode node = page.getData().get(PUBLISHED);
    switch (node.getType()) {
      case BOOLEAN:
        return node.as(Boolean.class);
      case NULL:
        return true;
      default:
        // other node types result in a rejection
        return false;
    }
  }
}
