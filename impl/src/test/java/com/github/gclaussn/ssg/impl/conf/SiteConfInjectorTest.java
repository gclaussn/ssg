package com.github.gclaussn.ssg.impl.conf;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.github.gclaussn.ssg.conf.SiteProperty;

public class SiteConfInjectorTest {

  private SiteConfInjector injector;
  
  private SiteConfImpl conf;
  private Map<String, String> env;

  @Before
  public void setUp() {
    conf = new SiteConfImpl();
    env = new HashMap<>();

    injector = new SiteConfInjector(conf, env);
  }

  @Test
  public void testReplaceVariable() {
    env.put("SSG_HOME", "/opt/ssg");

    assertThat(injector.replaceVariables("${SSG_HOME}"), equalTo(env.get("SSG_HOME")));
  }

  @Test
  public void shouldInject() {
    env.put("SSG_HOME", "/opt/ssg");

    InjectionTarget target = injector.inject(new InjectionTarget(), Collections.emptyMap());
    assertThat(target.template, equalTo("/opt/ssg/templates/default"));
  }

  private static class InjectionTarget {

    @SiteProperty(name = "ssg.init.template", defaultValue = "${SSG_HOME}/templates/default")
    private String template;
  }
}
