package com.github.gclaussn.ssg.impl.model;

import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.SourceType;

public class SourceImpl implements Source {

  private final String id;
  private final SourceType type;

  public SourceImpl(SourceType type, String id) {
    this.type = type;
    this.id = id;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof Source)) {
      return false;
    }

    Source source = (Source) obj;
    return type == source.getType() && id.equals(source.getId());
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public SourceType getType() {
    return type;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    if (type == null) {
      return type.name();
    } else {
      return String.format("%s[id=%s]", getType(), id);
    }
  }
}
