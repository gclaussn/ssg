package com.github.gclaussn.ssg.data;

import java.util.Map;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.impl.data.PageDataBuilderImpl;

public interface PageData {

  static final String ID = "_id";
  /** Key of the Markdown code, if the {@link Page} is defined by a Markdown file. */
  static final String MARKDOWN = "_md";
  static final String SET_ID = "_setId";
  static final String SUB_ID = "_subId";
  static final String URL = "_url";

  static PageDataBuilder builder() {
    return new PageDataBuilderImpl();
  }

  /**
   * Returns an empty page data instance, which cannot be modified.
   * 
   * @return Empty page data.
   */
  static PageData empty() {
    return PageDataBuilderImpl.EMPTY;
  }

  static PageData of(Map<String, Object> data) {
    return builder().putRoot(data).build();
  }

  PageDataNode get(String location);

  PageDataNode getRoot();

  Map<String, Object> getRootMap();

  boolean has(String location);

  /**
   * Returns {@code true} if the page data contains no {@link PageDataNode}s.
   * 
   * @return {@code true} if no nodes are mapped. Otherwise {@code false}.
   */
  boolean isEmpty();
}
