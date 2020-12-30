package com.github.gclaussn.ssg.conf;

public enum SitePropertyType {

  BOOLEAN,
  DOUBLE,
  ENUM,
  INTEGER,
  LONG,
  OTHER,
  STRING;

  public static SitePropertyType of(Class<?> type) {
    if (type == String.class) {
      return STRING;
    } else if (type == Boolean.class) {
      return BOOLEAN;
    } else if (type == Integer.class) {
      return INTEGER;
    } else if (type == Long.class) {
      return LONG;
    } else if (type == Double.class) {
      return DOUBLE;
    } else if (type.isEnum()) {
      return ENUM;
    } else {
      return OTHER;
    }
  }
}
