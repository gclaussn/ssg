package com.github.gclaussn.ssg.impl.npm;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.gclaussn.ssg.npm.NodePackageInfo;

class NodePackageInfoDeserializer extends JsonDeserializer<NodePackageInfo> {

  @Override
  public NodePackageInfo deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    return ctxt.readValue(p, NodePackageInfoDTO.class);
  }
}
