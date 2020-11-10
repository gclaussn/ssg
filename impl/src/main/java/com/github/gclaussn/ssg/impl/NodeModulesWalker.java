package com.github.gclaussn.ssg.impl;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

class NodeModulesWalker extends SimpleFileVisitor<Path> {

  private final List<PathMatcher> includeMatchers;

  private final List<Path> files;

  NodeModulesWalker(NodeModules nodeModules) {
    files = new LinkedList<>();

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
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
    for (PathMatcher includeMatcher : includeMatchers) {
      if (includeMatcher.matches(file)) {
        files.add(file);
        break;
      }
    }

    return FileVisitResult.CONTINUE;
  }

  protected List<Path> walk(Path path) throws IOException {
    files.clear();

    Files.walkFileTree(path, this);

    return files;
  }
}
