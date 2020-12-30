package com.github.gclaussn.ssg.server.domain.plugin;

import java.util.List;
import java.util.stream.Collectors;

import com.github.gclaussn.ssg.plugin.SitePluginGoalDesc;

public class SitePluginGoalDTO {

  public static SitePluginGoalDTO of(SitePluginGoalDesc desc) {
    SitePluginGoalDTO target = new SitePluginGoalDTO();
    target.documentation = desc.getDocumentation();
    target.id = desc.getId();
    target.name = desc.getName();
    target.properties = desc.getProperties().stream().map(SitePropertyDTO::of).collect(Collectors.toList());
    target.typeName = desc.getTypeName();

    return target;
  }

  private String documentation;
  private String id;
  private String name;
  private List<SitePropertyDTO> properties;
  private String typeName;

  public String getDocumentation() {
    return documentation;
  }

  public String getId() {
    return id;
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
