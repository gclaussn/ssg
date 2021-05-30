package com.github.gclaussn.ssg.builtin.action;

import static com.github.gclaussn.ssg.test.CustomMatcher.isDirectory;
import static com.github.gclaussn.ssg.test.CustomMatcher.isFile;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Rule;
import org.junit.Test;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.test.SiteRule;

public class InitActionTest {

  @Rule
  public SiteRule rule = new SiteRule(this);

  @Test
  public void shouldInitFromClasspath() {
    Site site = Site.from(rule.getPath());

    InitAction.builder().template(String.format("classpath:%s/default", rule.getResourceName())).execute(site);
    assertThat(site.getPath().resolve(Site.MODEL_NAME), isFile());
    assertThat(site.getSourcePath(), isDirectory());
    assertThat(site.getSourcePath().resolve("index.yaml"), isFile());
    assertThat(site.getPublicPath(), isDirectory());
    assertThat(site.getPublicPath().resolve("index.css"), isFile());
  }

  @Test
  public void shouldInitFromFile() {
    Site site = Site.from(rule.getPath());

    InitAction.builder().template(String.format("./src/test/resources/%s/default", rule.getResourceName())).execute(site);
    assertThat(site.getPath().resolve(Site.MODEL_NAME), isFile());
    assertThat(site.getSourcePath(), isDirectory());
    assertThat(site.getSourcePath().resolve("index.yaml"), isFile());
    assertThat(site.getPublicPath(), isDirectory());
    assertThat(site.getPublicPath().resolve("index.css"), isFile());
  }
}
