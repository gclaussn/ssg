package com.github.gclaussn.ssg.impl.conf;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.github.gclaussn.ssg.SiteErrorType;
import com.github.gclaussn.ssg.conf.SiteProperty;
import com.github.gclaussn.ssg.conf.SitePropertyDesc;
import com.github.gclaussn.ssg.conf.SitePropertyType;
import com.github.gclaussn.ssg.conf.TypeDesc;

public class SiteConfImplTest {

  private SiteConfImpl conf;

  @Before
  public void setUp() {
    conf = new SiteConfImpl();
  }

  @Test
  public void shouldDescribe() {
    TypeDesc typeDesc = conf.describe(TypeWithoutModelFile.class);
    assertThat(typeDesc, notNullValue());
    assertThat(typeDesc.getDocumentation(), nullValue());
    assertThat(typeDesc.getName(), equalTo("TypeWithoutModelFile"));
    assertThat(typeDesc.getProperties().size(), is(6));

    SitePropertyDesc propertyDesc;

    propertyDesc = typeDesc.getProperties().get(0);
    assertThat(propertyDesc.getDefaultValue(), nullValue());
    assertThat(propertyDesc.getDocumentation(), nullValue());
    assertThat(propertyDesc.getName(), equalTo("boolean"));
    assertThat(propertyDesc.getType(), is(SitePropertyType.BOOLEAN));
    assertThat(propertyDesc.getTypeName(), equalTo(Boolean.class.getName()));
    assertThat(propertyDesc.getValue(), nullValue());
    assertThat(propertyDesc.getVariable(), nullValue());
    assertThat(propertyDesc.getVariableName(), equalTo("BOOLEAN"));
    assertThat(propertyDesc.isMasked(), is(false));
    assertThat(propertyDesc.isRequired(), is(true));

    propertyDesc = typeDesc.getProperties().get(5);
    assertThat(propertyDesc.getDefaultValue(), equalTo("default string"));
    assertThat(propertyDesc.getType(), is(SitePropertyType.STRING));
    assertThat(propertyDesc.getTypeName(), equalTo(String.class.getName()));
    assertThat(propertyDesc.isRequired(), is(false));
  }

  private static class TypeWithoutModelFile {

    @SiteProperty(name = "boolean")
    private Boolean booleanValue;
    @SiteProperty(name = "double")
    private Double doubleValue;
    @SiteProperty(name = "enum")
    private SiteErrorType enumValue;
    @SiteProperty(name = "integer")
    private Integer integerValue;
    @SiteProperty(name = "long")
    private Long longValue;
    @SiteProperty(name = "string", defaultValue = "default string")
    private String stringValue;
  }
}
