package com.github.gclaussn.ssg.server.domain.plugin;

import com.github.gclaussn.ssg.conf.SitePropertyDesc;
import com.github.gclaussn.ssg.conf.SitePropertyType;

public class SitePropertyDTO {

  public static SitePropertyDTO of(SitePropertyDesc desc) {
    SitePropertyDTO target = new SitePropertyDTO();
    target.defaultValue = desc.getDefaultValue();
    target.documentation = desc.getDocumentation();
    target.masked = desc.isMasked();
    target.name = desc.getName();
    target.required = desc.isRequired();
    target.type = desc.getType();
    target.typeName = desc.getTypeName();
    target.value = desc.getValue();
    target.variable = desc.getVariable();
    target.variableName = desc.getVariableName();

    return target;
  }

  private String defaultValue;
  private String documentation;
  private boolean masked;
  private String name;
  private boolean required;
  private SitePropertyType type;
  private String typeName;
  private String value;
  private String variable;
  private String variableName;

  public String getDefaultValue() {
    return defaultValue;
  }

  public String getDocumentation() {
    return documentation;
  }

  public String getName() {
    return name;
  }

  public SitePropertyType getType() {
    return type;
  }

  public String getTypeName() {
    return typeName;
  }

  public String getValue() {
    return value;
  }

  public String getVariable() {
    return variable;
  }

  public String getVariableName() {
    return variableName;
  }

  public boolean isMasked() {
    return masked;
  }

  public boolean isRequired() {
    return required;
  }
}
