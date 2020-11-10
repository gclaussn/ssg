package com.github.gclaussn.ssg.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.github.gclaussn.ssg.conf.SiteProperty;

public class SiteConfInjectorTest {

  private SiteConfInjector injector;

  @Test
  public void testGetEnvironmentVariableName() {
    injector = new SiteConfInjector(Collections.emptyMap());

    assertThat(injector.getEnvironmentVariableName("test.prop"), equalTo("TEST_PROP"));
  }

  @Test
  public void testReplaceVariable() {
    Map<String, String> env = new HashMap<>();
    env.put("SSG_HOME", "/opt/ssg");

    injector = new SiteConfInjector(Collections.emptyMap(), env);

    assertThat(injector.replaceVariables("${SSG_HOME}"), equalTo(env.get("SSG_HOME")));
  }

  @Test
  public void shouldInject() {
    Map<String, String> env = new HashMap<>();
    env.put("SSG_HOME", "/opt/ssg");

    injector = new SiteConfInjector(Collections.emptyMap(), env);

    InjectionTarget target = injector.inject(new InjectionTarget(), Collections.emptyMap());
    assertThat(target.template, equalTo("/opt/ssg/templates/default"));
  }

  private static class InjectionTarget {

    @SiteProperty(name = "ssg.init.template", defaultValue = "${SSG_HOME}/templates/default")
    private String template;
  }
}
