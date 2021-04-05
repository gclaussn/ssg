package com.github.gclaussn.ssg.impl.npm;

import com.fasterxml.jackson.annotation.JsonProperty;

class NodePackageDistDTO {

  @JsonProperty("shasum")
  protected String checksum;
  @JsonProperty("tarball")
  protected String url;
}
