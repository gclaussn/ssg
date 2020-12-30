package com.github.gclaussn.ssg.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import com.github.gclaussn.ssg.conf.SitePropertyDesc;
import com.github.gclaussn.ssg.conf.TypeDesc;

class TypeDescImpl implements TypeDesc {

  private final TypeDescModel model;

  private final Set<SitePropertyDesc> properties;

  TypeDescImpl(TypeDescModel model) {
    this.model = model;

    // use LinkedHashSet to preserve the property (field) order, used within the implementation
    properties = new LinkedHashSet<>();
  }

  protected void addProperty(SitePropertyDesc property) {
    properties.add(property);
  }

  @Override
  public String getDocumentation() {
    return model.documentation;
  }

  @Override
  public String getName() {
    return model.name;
  }

  @Override
  public Set<SitePropertyDesc> getProperties() {
    return properties;
  }
}
