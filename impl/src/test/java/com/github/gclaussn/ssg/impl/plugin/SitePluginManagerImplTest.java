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
  public void testBuildPluginGoalId() {
    assertThat(manager.buildPluginGoalId("TestGoal"), equalTo("test"));
    assertThat(manager.buildPluginGoalId("TestSomethingGoal"), equalTo("test-something"));
  }
}
