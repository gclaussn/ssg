package com.github.gclaussn.ssg.sites;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.SiteErrorType;

public class SitesTest {

  @Rule
  public TestName testName = new TestName();

  private Path sitePath;

  @Before
  public void setUp() {
    String name = WordUtils.uncapitalize(testName.getMethodName().replace("test", ""));

    sitePath = Paths.get("src/test/resources/sites/").resolve(name);
  }

  @Test
  public void testSiteModelError() {
    List<SiteError> errors = Site.from(sitePath).load();
    assertThat(errors.size(), is(1));

    SiteError error = errors.get(0);
    assertThat(error.getCause(), notNullValue());
    assertThat(error.getMessage(), notNullValue());
    assertThat(error.getSource().isPresent(), is(true));
    assertThat(error.getSource().get().getId(), nullValue());
    assertThat(error.getSource().get().getType(), nullValue());
    assertThat(error.getLocation().isPresent(), is(true));
    assertThat(error.getLocation().get().getPath(), equalTo(sitePath.resolve(Site.MODEL_NAME)));
    assertThat(error.getLocation().get().getLine(), is(1));
    assertThat(error.getLocation().get().getColumn(), is(1));
    assertThat(error.getType(), is(SiteErrorType.MODEL));
  }

  @Test
  public void testTemplateErrors() {
    List<SiteError> errors = Site.from(sitePath).load();
    assertThat(errors.size(), is(1));
  }

  @Test
  public void testWithoutSiteModel() {
    List<SiteError> errors = Site.from(sitePath).load();
    assertThat(errors.size(), is(1));

    SiteError error = errors.get(0);
    assertThat(error.getCause(), notNullValue());
    assertThat(error.getMessage(), notNullValue());
    assertThat(error.getSource().isPresent(), is(true));
    assertThat(error.getSource().get().getId(), nullValue());
    assertThat(error.getSource().get().getType(), nullValue());
    assertThat(error.getLocation().isPresent(), is(true));
    assertThat(error.getLocation().get().getPath(), equalTo(sitePath.resolve(Site.MODEL_NAME)));
    assertThat(error.getLocation().get().getLine(), is(-1));
    assertThat(error.getLocation().get().getColumn(), is(-1));
    assertThat(error.getType(), is(SiteErrorType.IO));
  }

  /**
   * Valid: without pages and without source directory
   */
  @Test
  public void testWithoutSourceDirectory() {
    List<SiteError> errors = Site.from(sitePath).load();
    assertThat(errors.isEmpty(), is(true));
  }
}
