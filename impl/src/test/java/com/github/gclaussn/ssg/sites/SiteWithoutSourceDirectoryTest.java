package com.github.gclaussn.ssg.sites;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.test.SiteRule;

public class SiteWithoutSourceDirectoryTest {

  @Rule
  public SiteRule rule = new SiteRule(this);

  /**
   * Valid: without pages and without source directory
   */
  @Test
  public void shouldLoad() {
    Site site = rule.init();

    List<SiteError> errors = site.load();
    assertThat(errors.isEmpty(), is(true));
  }
}
