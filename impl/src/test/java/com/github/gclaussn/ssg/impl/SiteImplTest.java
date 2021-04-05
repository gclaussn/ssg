package com.github.gclaussn.ssg.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import com.github.gclaussn.ssg.Site;

public class SiteImplTest {

  private SiteImpl site;

  @Before
  public void setUp() {
    String packageName = SiteImplTest.class.getPackage().getName().replace('.', '/');
    Path sitePath = Paths.get("./src/test/resources").resolve(packageName);

    site = new SiteImpl(new SiteBuilderImpl(), sitePath);
  }

  @Test
  public void testGetConfiguration() {
    assertThat(site.getConfiguration(), notNullValue());
  }

  @Test
  public void testGetGenerator() {
    assertThat(site.getGenerator(), notNullValue());
  }

  @Test
  public void testGetPath() {
    assertThat(site.getPath(), equalTo(site.getPath()));

    assertThat(site.getPublicPath(), equalTo(site.getPath().resolve(Site.PUBLIC)));
    assertThat(site.getSourcePath(), equalTo(site.getPath().resolve(Site.SOURCE)));
    assertThat(site.getOutputPath(), equalTo(site.getPath().resolve(Site.OUTPUT)));
  }

  @Test
  public void testGetPluginManager() {
    assertThat(site.getPluginManager(), notNullValue());
  }
}
