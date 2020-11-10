package com.github.gclaussn.ssg.builtin;

import static com.github.gclaussn.ssg.test.CustomMatcher.isDirectory;
import static com.github.gclaussn.ssg.test.CustomMatcher.isFile;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.github.gclaussn.ssg.Site;

public class DefaultPluginTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder(Paths.get("./target").toFile());

  private Path sitePath;

  @Before
  public void setUp() {
    sitePath = temporaryFolder.getRoot().toPath();
  }

  @Test
  public void shouldInitFromClasspath() {
    Site site = Site.builder().build(sitePath);

    site.execute("init", Collections.singletonMap("ssg.init.template", "classpath:sites/init/default"));
    assertThat(sitePath.resolve(Site.MODEL_NAME), isFile());
    assertThat(site.getSourcePath(), isDirectory());
    assertThat(site.getSourcePath().resolve("index.yaml"), isFile());
    assertThat(site.getPublicPath(), isDirectory());
    assertThat(site.getPublicPath().resolve("index.css"), isFile());
  }

  @Test
  public void shouldInitFromFile() {
    Site site = Site.builder().build(sitePath);

    site.execute("init", Collections.singletonMap("ssg.init.template", "./src/test/resources/sites/init/default"));
    assertThat(sitePath.resolve(Site.MODEL_NAME), isFile());
    assertThat(site.getSourcePath(), isDirectory());
    assertThat(site.getSourcePath().resolve("index.yaml"), isFile());
    assertThat(site.getPublicPath(), isDirectory());
    assertThat(site.getPublicPath().resolve("index.css"), isFile());
  }

  @Test
  public void shouldCopyOutput() throws IOException {
    Site site = Site.builder().build(sitePath);

    site.execute("init", Collections.singletonMap("ssg.init.template", "classpath:sites/init/default"));

    site.load();
    site.generate();

    Path target = Files.createDirectories(site.getPath().resolve("target"));

    site.execute("copy-output", Collections.singletonMap("ssg.cp.target", target.toAbsolutePath().toString()));
    assertThat(target.resolve("index.html"), isFile());
    assertThat(target.resolve("index.css"), isFile());
    assertThat(target.resolve("node_modules"), isDirectory());
    assertThat(target.resolve("node_modules/test"), isDirectory());
    assertThat(target.resolve("node_modules/test/styles.css"), isFile());
  }
}
