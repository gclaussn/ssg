package com.github.gclaussn.ssg.impl.npm;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.npm.NodePackage;
import com.github.gclaussn.ssg.npm.NodePackageManager;
import com.github.gclaussn.ssg.npm.NodePackageSpec;

public class NodePackageSpecDeserializer extends JsonDeserializer<NodePackageSpec> {

  protected static final String FIELD_INCLUDES = "includes";
  protected static final String FIELD_PACKAGES = "packages";

  private final Site site;
  
  public NodePackageSpecDeserializer(Site site) {
    this.site = site;
  }

  @Override
  public NodePackageSpec deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    ObjectCodec oc = p.getCodec();

    JsonNode jsonNode = oc.readTree(p);

    NodePackageSpecImpl nodePackageSpec = new NodePackageSpecImpl();
    nodePackageSpec.path = site.getPath().resolve(NodePackageManager.NODE_MODULES);
    nodePackageSpec.includes = getIncludes(jsonNode);
    nodePackageSpec.packages = getPackages(jsonNode);

    return nodePackageSpec;
  }

  protected List<String> getIncludes(JsonNode jsonNode) {
    if (!jsonNode.has(FIELD_INCLUDES)) {
      return Collections.emptyList();
    }

    JsonNode includesNode = jsonNode.get(FIELD_INCLUDES);
    if (!includesNode.isArray()) {
      return Collections.emptyList();
    }

    List<String> includes = new LinkedList<>();

    for (int i = 0; i < includesNode.size(); i++) {
      JsonNode value = includesNode.get(i);

      if (!value.isTextual()) {
        continue;
      }

      includes.add(value.asText());
    }

    return includes;
  }

  protected List<NodePackage> getPackages(JsonNode jsonNode) {
    if (!jsonNode.has(FIELD_PACKAGES)) {
      return Collections.emptyList();
    }

    JsonNode packagesNode = jsonNode.get(FIELD_PACKAGES);
    if (!packagesNode.isObject()) {
      return Collections.emptyList();
    }
    
    List<NodePackage> packages = new LinkedList<>();

    Iterator<String> fieldNames = packagesNode.fieldNames();
    while(fieldNames.hasNext()) {
      String packageName = fieldNames.next();
      
      JsonNode versionNode = packagesNode.get(packageName);
      if (!versionNode.isTextual()) {
        continue;
      }

      NodePackageDTO nodePackage = new NodePackageDTO();
      nodePackage.name = packageName;
      nodePackage.version = versionNode.asText();

      packages.add(nodePackage);
    }

    return packages;
  }
}
