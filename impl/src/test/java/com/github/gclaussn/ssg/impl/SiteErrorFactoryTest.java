package com.github.gclaussn.ssg.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.SiteErrorType;

public class SiteErrorFactoryTest {

  private SiteErrorFactory errorFactory;
  private SiteError error;

  @Before
  public void setUp() {
    Path sitePath = Paths.get("");

    errorFactory = new SiteErrorFactory(Site.from(sitePath));
  }

  @Test
  public void testOutputDirectoryNotCreated() {
    error = errorFactory.outputDirectoryNotCreated(new IOException());
    assertThat(error.getCause(), notNullValue());
    assertThat(error.getMessage(), notNullValue());
    assertThat(error.getSource().isEmpty(), is(true));
    assertThat(error.getType(), is(SiteErrorType.IO));
  }
}
