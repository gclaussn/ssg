package com.github.gclaussn.ssg.builtin.action;

import static com.github.gclaussn.ssg.test.CustomMatcher.isDirectory;
import static com.github.gclaussn.ssg.test.CustomMatcher.isFile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.test.SiteRule;

public class CpActionTest {

  @Rule
  public SiteRule rule = new SiteRule(this);

  @Test
  public void shouldCopyOutput() throws IOException {
    Site site = Site.from(rule.getPath());

    String resourceName = rule.getResourceName(InitActionTest.class);
    InitAction.builder().template(String.format("classpath:%s/default", resourceName)).execute(site);

    List<SiteError> errors;

    errors = site.load();
    assertThat(errors, hasSize(0));
    errors = site.generate();
    assertThat(errors, hasSize(0));

    Path target = rule.newFolder("target").toPath();

    CpAction.builder().target(target.toAbsolutePath().toString()).execute(site);
    assertThat(target.resolve("index.html"), isFile());
    assertThat(target.resolve("index.css"), isFile());
    assertThat(target.resolve("node_modules"), isDirectory());
    assertThat(target.resolve("node_modules/test"), isDirectory());
    assertThat(target.resolve("node_modules/test/styles.css"), isFile());
  }
}
