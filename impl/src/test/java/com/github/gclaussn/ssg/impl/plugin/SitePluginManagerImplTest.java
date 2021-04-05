package com.github.gclaussn.ssg.impl.plugin;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class SitePluginManagerImplTest {

  private SitePluginManagerImpl manager;

  @Before
  public void setUp() {
    manager = new SitePluginManagerImpl();
  }

  @Test
  public void testBuildPluginActionId() {
    assertThat(manager.buildPluginActionId("TestAction"), equalTo("test"));
    assertThat(manager.buildPluginActionId("TestSomethingAction"), equalTo("test-something"));
  }
}
