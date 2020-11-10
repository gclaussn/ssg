package com.github.gclaussn.ssg.impl;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

public class EventDrivenSiteImplTest {

  private EventDrivenSiteImpl site;

  @Before
  public void setUp() {
    Path sitePath = Paths.get("./src/test/resources")
        .resolve(SiteImplTest.class.getPackage().getName().replace('.', '/'));

    site = new EventDrivenSiteImpl(sitePath, new SiteConfImpl());
  }

  @Test
  public void testExtractSetId() {
    site.extractSetId("posts/hello-world");
  }
}
