package com.github.gclaussn.ssg.impl;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

class NodeModulesMatcher implements Predicate<Path> {

  private final List<PathMatcher> includeMatchers;

  NodeModulesMatcher(NodeModules nodeModules) {
    includeMatchers = new LinkedList<>();

    List<String> includes = orElse(nodeModules.includes, Collections::emptyList);
    for (String include : includes) {
      String pattern = String.format("glob:%s", include);
      includeMatchers.add(FileSystems.getDefault().getPathMatcher(pattern));
    }
  }

  private <T> T orElse(T value, Supplier<T> defaultValueSupplier) {
    return value != null ? value : defaultValueSupplier.get();
  }

  @Override
  public boolean test(Path filePath) {
    if (includeMatchers.isEmpty()) {
      return true;
    }

    for (PathMatcher includeMatcher : includeMatchers) {
      if (includeMatcher.matches(filePath)) {
        return true;
      }
    }

    return false;
  }
}
