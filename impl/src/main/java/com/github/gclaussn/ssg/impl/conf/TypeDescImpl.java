package com.github.gclaussn.ssg.impl.conf;

import java.util.LinkedList;
import java.util.List;

import com.github.gclaussn.ssg.conf.SitePropertyDesc;
import com.github.gclaussn.ssg.conf.TypeDesc;

class TypeDescImpl implements TypeDesc {

  private final TypeDescModel model;

  private final List<SitePropertyDesc> properties;

  TypeDescImpl(TypeDescModel model) {
    this.model = model;

    properties = new LinkedList<>();
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
  public List<SitePropertyDesc> getProperties() {
    return properties;
  }
}
