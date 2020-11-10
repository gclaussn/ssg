package com.github.gclaussn.ssg.impl.data;

import java.util.HashMap;
import java.util.Map;

import com.github.gclaussn.ssg.data.PageData;
import com.github.gclaussn.ssg.data.PageDataNode;

class PageDataImpl implements PageData {

  protected final Map<String, Object> root;
  protected final PageDataNode rootNode;

  PageDataImpl() {
    this(new HashMap<>());
  }

  PageDataImpl(Map<String, Object> data) {
    root = data;
    rootNode = new PageDataNodeImpl(data);
  }

  protected Map<String, Object> find(String location) {
    PageDataNode node = rootNode;

    String[] keys = location.split("/");
    for (String key : keys) {
      Map<String, Object> map = node.asMap();
      if (!map.containsKey(key) || !node.isMap()) {
        map.put(key, new HashMap<>());
      }

      node = node.get(key);
    }

    return node.asMap();
  }

  @Override
  public PageDataNode get(String path) {
    PageDataNode node = rootNode;

    String[] keys = path.split("/");
    for (String key : keys) {
      if (!node.isMap()) {
        return null;
      }

      node = node.get(key);
    }

    return node;
  }

  @Override
  public PageDataNode getRoot() {
    return rootNode;
  }

  @Override
  public Map<String, Object> getRootMap() {
    return root;
  }

  @Override
  public boolean has(String location) {
    PageDataNode node = rootNode;

    String[] keys = location.split("/");
    for (String key : keys) {
      if (!node.isMap()) {
        return false;
      }

      node = node.get(key);
      if (node.isNull()) {
        return false;
      }
    }

    return true;
  }

  @Override
  public boolean isEmpty() {
    return root.isEmpty();
  }
}
