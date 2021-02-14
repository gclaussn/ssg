package com.github.gclaussn.ssg.impl.error;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.error.SiteError;
import com.github.gclaussn.ssg.error.SiteErrorType;

public class SiteErrorBuilderImplTest {

  private SiteErrorBuilderImpl errorBuilder;
  private SiteError error;

  @Before
  public void setUp() {
    Path sitePath = Paths.get("");

    errorBuilder = new SiteErrorBuilderImpl(Site.from(sitePath));
  }

  @Test
  public void testOutputDirectoryNotCreated() {
    IOException cause = new IOException();

    error = errorBuilder.errorOutputDirectoryNotCreated(cause);
    assertThat(error.getCause(), is(cause));
    assertThat(error.getMessage(), notNullValue());
    assertThat(error.getSource().isEmpty(), is(true));
    assertThat(error.getType(), is(SiteErrorType.IO));
  }
}
