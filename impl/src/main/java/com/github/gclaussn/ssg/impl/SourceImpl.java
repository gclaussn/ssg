package com.github.gclaussn.ssg.impl;

import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.SourceType;

class SourceImpl implements Source {

  protected final String id;
  protected final SourceType type;

  SourceImpl(SourceType type, String id) {
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
    return id.equals(source.getId());
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
    if (type == SourceType.SITE) {
      return type.name();
    } else {
      return String.format("%s[id=%s]", getType(), id);
    }
  }
}
