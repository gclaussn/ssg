package com.github.gclaussn.ssg.data;

import java.util.List;
import java.util.Map;

public interface PageDataNode {

  <T> T as(Class<T> valueType);

  List<Object> asList();

  Map<String, Object> asMap();

  PageDataNode get(int index);

  PageDataNode get(String key);

  PageDataNodeType getType();

  boolean has(String key);

  boolean is(PageDataNodeType type);

  boolean isList();

  boolean isMap();

  boolean isNull();
}
