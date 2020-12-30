package com.github.gclaussn.ssg.impl;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import com.github.gclaussn.ssg.conf.SitePropertyDesc;
import com.github.gclaussn.ssg.conf.SitePropertyType;

class SitePropertyDescImpl implements SitePropertyDesc {

  protected String defaultValue;
  protected String documentation;
  protected boolean masked;
  protected String name;
  protected boolean required;
  protected SitePropertyType type;
  protected String typeName;
  protected String value;

  @Override
  public int compareTo(SitePropertyDesc desc) {
    return getName().compareTo(desc.getName());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof SitePropertyDesc)) {
      return false;
    }

    SitePropertyDesc desc = (SitePropertyDesc) obj;
    return name.equals(desc.getName());
  }

  @Override
  public String getDefaultValue() {
    return defaultValue;
  }

  @Override
  public String getDocumentation() {
    return documentation;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public SitePropertyType getType() {
    return type;
  }

  @Override
  public String getTypeName() {
    return typeName;
  }

  @Override
  public String getValue() {
    return value;
  }

  @Override
  public String getVariable() {
    String variable = System.getenv(getVariableName());
    return StringUtils.isNotBlank(variable) && masked ? StringUtils.repeat('*', 3) : variable;
  }

  @Override
  public String getVariableName() {
    return name.toUpperCase(Locale.ENGLISH).replace('.', '_');
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean isMasked() {
    return masked;
  }

  @Override
  public boolean isRequired() {
    return required;
  }
}
