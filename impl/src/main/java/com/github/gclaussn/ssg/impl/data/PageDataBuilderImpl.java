package com.github.gclaussn.ssg.impl.data;

import java.util.Collections;
import java.util.Map;

import com.github.gclaussn.ssg.data.PageData;
import com.github.gclaussn.ssg.data.PageDataBuilder;

public class PageDataBuilderImpl implements PageDataBuilder {

  public static final PageDataImpl EMPTY = new PageDataImpl(Collections.emptyMap());

  private PageDataImpl data;

  public PageDataBuilderImpl() {
    data = new PageDataImpl();
  }

  @Override
  public PageData build() {
    PageData built = data;

    data = new PageDataImpl();

    return built;
  }

  @Override
  public PageDataBuilder put(String location, Object data) {
    int index = location.lastIndexOf('/');

    Map<String, Object> map;
    if (index == -1) {
      map = this.data.root;
    } else {
      map = this.data.find(location.substring(0, index));
    }

    String key = location.substring(index + 1, location.length());

    map.put(key, data);

    return this;
  }

  @Override
  public PageDataBuilder putRoot(Map<String, Object> data) {
    this.data.root.putAll(data);
    return this;
  }

  @Override
  public PageDataBuilder putRoot(PageData data) {
    return putRoot(data.getRootMap());
  }
}
