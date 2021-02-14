package com.github.gclaussn.ssg.impl.data;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.github.gclaussn.ssg.data.PageDataNode;
import com.github.gclaussn.ssg.data.PageDataNodeType;

class PageDataNodeImpl implements PageDataNode {

  private final Object value;
  private final PageDataNodeType type;

  PageDataNodeImpl(Object value) {
    this.value = value;

    if (value == null) {
      type = PageDataNodeType.NULL;
      return;
    }

    Class<?> valueClass = value.getClass();
    if (valueClass == String.class) {
      type = PageDataNodeType.STRING;
    } else if (valueClass == Boolean.class) {
      type = PageDataNodeType.BOOLEAN;
    } else if (Map.class.isAssignableFrom(valueClass)) {
      type = PageDataNodeType.MAP;
    } else if (List.class.isAssignableFrom(valueClass)) {
      type = PageDataNodeType.LIST;
    } else {
      type = PageDataNodeType.OTHER;
    }
  }

  @Override
  public <T> T as(Class<T> valueType) {
    Objects.requireNonNull(valueType, "value type is null");

    try {
      return valueType.cast(value);
    } catch (ClassCastException e) {
      throw new IllegalStateException(String.format("Type %s cannot be casted to %s", type, valueType));
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Object> asList() {
    ensure(PageDataNodeType.LIST);
    return (List<Object>) value;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Map<String, Object> asMap() {
    ensure(PageDataNodeType.MAP);
    return (Map<String, Object>) value;
  }

  /**
   * Ensures that the node has the expected type.<br>
   * If the node is of a different type, an {@link IllegalStateException} is thrown.
   * 
   * @param expected The expected node type.
   */
  protected void ensure(PageDataNodeType expected) {
    if (!is(expected)) {
      throw new IllegalStateException(String.format("Expected type %s, but was %s", expected, type));
    }
  }

  @Override
  public PageDataNode get(int index) {
    return new PageDataNodeImpl(asList().get(index));
  }

  @Override
  public PageDataNode get(String key) {
    return new PageDataNodeImpl(asMap().get(key));
  }

  @Override
  public PageDataNodeType getType() {
    return type;
  }

  @Override
  public boolean has(String key) {
    return asMap().containsKey(key);
  }

  @Override
  public boolean is(PageDataNodeType type) {
    return this.type == type;
  }

  @Override
  public boolean isList() {
    return is(PageDataNodeType.LIST);
  }

  @Override
  public boolean isMap() {
    return is(PageDataNodeType.MAP);
  }

  @Override
  public boolean isNull() {
    return is(PageDataNodeType.NULL);
  }
}
