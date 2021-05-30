package com.github.gclaussn.ssg.impl;

import java.util.Objects;

class HashCode {

  private final String pageId;
  private final String pageDataSelectorId;

  private final int value;

  HashCode(String pageId, String pageDataSelectorId, int value) {
    this.pageId = pageId;
    this.pageDataSelectorId = pageDataSelectorId;
    this.value = value;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof HashCode)) {
      return false;
    }

    HashCode hashCode = (HashCode) obj;
    return pageId.equals(hashCode.pageId) && pageDataSelectorId.equals(hashCode.pageDataSelectorId);
  }

  public int getValue() {
    return value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageId, pageDataSelectorId);
  }
}
