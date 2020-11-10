package com.github.gclaussn.ssg.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class TypeLookup<T> {

  /** Mapping of type names to types. */
  private final Map<String, Class<? extends T>> typeMap;

  /** Mapping of unambiguous simple type names to types names. */
  private final Map<String, String> typeNameMap;

  TypeLookup(Set<Class<? extends T>> types) {
    typeMap = new HashMap<>();
    typeNameMap = new HashMap<>();

    Set<String> ambiguousNames = new HashSet<>();
    for (Class<? extends T> type : types) {
      typeMap.put(type.getName(), type);

      boolean ambiguous = typeNameMap.put(type.getSimpleName(), type.getName()) != null;
      if (ambiguous) {
        ambiguousNames.add(type.getSimpleName());
      }
    }

    ambiguousNames.forEach(typeNameMap::remove);
    ambiguousNames.clear();
  }

  protected Class<? extends T> lookup(String typeName) {
    String className;
    if (typeNameMap.containsKey(typeName)) {
      // use simple type name mapping
      className = typeNameMap.get(typeName);
    } else {
      className = typeName;
    }

    return typeMap.get(className);
  }
}
