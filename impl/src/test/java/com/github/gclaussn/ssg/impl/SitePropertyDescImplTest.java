package com.github.gclaussn.ssg.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class SitePropertyDescImplTest {

  @Test
  public void testGetEnvironmentVariableName() {
    SitePropertyDescImpl desc = new SitePropertyDescImpl();
    desc.name = "test.prop";

    assertThat(desc.getVariableName(), equalTo("TEST_PROP"));
  }
}
