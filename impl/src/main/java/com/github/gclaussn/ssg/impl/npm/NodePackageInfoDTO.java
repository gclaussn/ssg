package com.github.gclaussn.ssg.impl.npm;

import com.github.gclaussn.ssg.npm.NodePackageInfo;

class NodePackageInfoDTO extends NodePackageDTO implements NodePackageInfo {

  protected NodePackageDistDTO dist;

  @Override
  public String getChecksum() {
    return dist.checksum;
  }

  @Override
  public String getFileName() {
    return dist.url.substring(dist.url.lastIndexOf('/') + 1, dist.url.length());
  }

  @Override
  public String getUrl() {
    return dist.url;
  }
}
