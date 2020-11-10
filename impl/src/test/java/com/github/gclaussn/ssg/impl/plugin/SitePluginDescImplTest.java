package com.github.gclaussn.ssg.impl.plugin;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.github.gclaussn.ssg.plugin.SitePlugin;

public class SitePluginDescImplTest {

  private SitePluginDescImpl desc;

  @Before
  public void setUp() {
    SitePlugin plugin = Mockito.mock(SitePlugin.class);

    desc = new SitePluginDescImpl(plugin);
  }

  @Test
  public void testExtractGoalShortName() {
    assertThat(desc.extractGoalShortName("TestGoal"), equalTo("test"));
    assertThat(desc.extractGoalShortName("TestSomethingGoal"), equalTo("test-something"));
  }
}
