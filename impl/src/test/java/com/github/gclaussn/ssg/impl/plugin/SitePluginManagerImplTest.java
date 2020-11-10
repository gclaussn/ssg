package com.github.gclaussn.ssg.impl.plugin;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.conf.SiteConf;

public class SitePluginManagerImplTest {

  private SitePluginManagerImpl manager;

  @Before
  public void setUp() {
    Site site = Mockito.mock(Site.class);
    when(site.getConf()).thenReturn(Mockito.mock(SiteConf.class));

    manager = new SitePluginManagerImpl(site);
  }

  @Test
  public void testExtractPluginGoalName() {
    assertThat(manager.extractPluginGoalName("test:goal"), equalTo("goal"));
  }

  @Test
  public void testExtractPluginName() {
    assertThat(manager.extractPluginName("test:goal"), equalTo("test"));
  }
}
