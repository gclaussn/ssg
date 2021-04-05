package com.github.gclaussn.ssg.impl.npm;

import com.github.gclaussn.ssg.npm.NodePackage;

class NodePackageDTO implements NodePackage {

  protected String name;
  protected String version;

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getVersion() {
    return version;
  }

  @Override
  public String toString() {
    return String.format("%s:%s", name, version);
  }
}
