package com.github.gclaussn.ssg.impl.model;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import com.github.gclaussn.ssg.model.NodeModules;

class NodeModulesImpl implements NodeModules {

  protected List<String> includes;

  @Override
  public List<String> getIncludes() {
    return includes != null ? new LinkedList<>(includes) : Collections.emptyList();
  }

  @Override
  public Predicate<Path> getMatcher() {
    return new NodeModulesMatcher(this);
  }
}
