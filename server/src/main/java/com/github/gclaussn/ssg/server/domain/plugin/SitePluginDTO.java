package com.github.gclaussn.ssg.server.domain.plugin;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.gclaussn.ssg.plugin.SitePluginDesc;

public class SitePluginDTO {

  public static SitePluginDTO of(SitePluginDesc desc) {
    SitePluginDTO target = new SitePluginDTO();
    target.actions = new LinkedList<>(desc.getActions());
    target.documentation = desc.getDocumentation();
    target.name = desc.getName();
    target.properties = desc.getProperties().stream().map(SitePropertyDTO::of).collect(Collectors.toList());
    target.typeName = desc.getTypeName();

    return target;
  }

  private List<String> actions;
  private String documentation;
  private String name;
  private List<SitePropertyDTO> properties;
  private String typeName;

  public List<String> getActions() {
    return actions;
  }

  public String getDocumentation() {
    return documentation;
  }

  public String getName() {
    return name;
  }

  public List<SitePropertyDTO> getProperties() {
    return properties;
  }

  public String getTypeName() {
    return typeName;
  }
}
