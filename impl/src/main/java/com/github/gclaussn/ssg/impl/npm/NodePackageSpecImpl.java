package com.github.gclaussn.ssg.impl.npm;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

import com.github.gclaussn.ssg.npm.NodePackage;
import com.github.gclaussn.ssg.npm.NodePackageSpec;

class NodePackageSpecImpl implements NodePackageSpec {

  protected Path path;

  protected List<String> includes;
  protected List<NodePackage> packages;

  @Override
  public List<String> getIncludes() {
    return includes;
  }

  @Override
  public Predicate<Path> getMatcher() {
    return new NodeModuleMatcher(this);
  }

  @Override
  public List<NodePackage> getPackages() {
    return packages;
  }
}
