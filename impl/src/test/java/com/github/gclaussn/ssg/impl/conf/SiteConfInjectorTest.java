package com.github.gclaussn.ssg.impl.conf;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.github.gclaussn.ssg.SiteErrorType;
import com.github.gclaussn.ssg.conf.SiteConsole;
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
    ServiceImpl service = new ServiceImpl();

    conf.getProperties().put("service", service);

    Map<String, Object> additionalProperties = new HashMap<>();
    additionalProperties.put("boolean", Boolean.TRUE.toString());
    additionalProperties.put("double", "1.1");
    additionalProperties.put("enum", SiteErrorType.UNKNOWN.name());
    additionalProperties.put("integer", "123");
    additionalProperties.put("long", "123456");
    additionalProperties.put("string", "abc");

    Target target = injector.inject(new Target(), additionalProperties);
    assertThat(target.service, is(service));
    assertThat(target.serviceImpl, is(service));
    assertThat(target.serviceObject, is(service));

    assertThat(target.notInjected, nullValue());

    assertThat(target.booleanValue, is(Boolean.TRUE));
    assertThat(target.doubleValue, equalTo(Double.valueOf(1.1)));
    assertThat(target.enumValue, is(SiteErrorType.UNKNOWN));
    assertThat(target.integerValue, is(123));
    assertThat(target.longValue, is(123456L));
    assertThat(target.stringValue, equalTo("abc"));
  }

  @Test
  public void shouldInjectDefaultValueWithVariable() {
    env.put("SSG_HOME", "/opt/ssg");

    TargetDefaultValueWithVariable target = injector.inject(new TargetDefaultValueWithVariable());
    assertThat(target.template, equalTo("/opt/ssg/templates/default"));
  }

  @Test
  public void shouldInjectGeneric() {
    List<String> list = new LinkedList<>();

    conf.getProperties().put("list", list);

    TargetGeneric target = injector.inject(new TargetGeneric());
    assertThat(target.list, is(list));
  }

  @Test
  public void shouldInjectConsole() {
    conf.setConsole(Mockito.mock(SiteConsole.class));

    TargetConsole target = injector.inject(new TargetConsole());
    assertThat(target.console, is(conf.getConsole()));
  }

  @Test
  public void shouldInjectConsoleFromProperties() {
    SiteConsole console = Mockito.mock(SiteConsole.class);

    TargetConsole target = injector.inject(new TargetConsole(), Collections.singletonMap(SiteConsole.PROPERTY_NAME, console));
    assertThat(target.console, is(console));
  }

  private static interface Service {
  }
  private static class ServiceImpl implements Service {
  }

  private static class Target {

    @SiteProperty(name = "service")
    private Service service;

    @SiteProperty(name = "service")
    private ServiceImpl serviceImpl;

    @SiteProperty(name = "service")
    private Object serviceObject;

    private Object notInjected;

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
    @SiteProperty(name = "string")
    private String stringValue;
  }

  private static class TargetDefaultValueWithVariable {

    @SiteProperty(name = "ssg.init.template", defaultValue = "${SSG_HOME}/templates/default")
    private String template;
  }

  private static class TargetGeneric {

    @SiteProperty(name = "list")
    private List<String> list;
  }

  private static class TargetConsole {

    private SiteConsole console;
  }
}
