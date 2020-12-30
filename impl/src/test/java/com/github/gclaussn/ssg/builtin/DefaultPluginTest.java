package com.github.gclaussn.ssg.builtin;

import static com.github.gclaussn.ssg.test.CustomMatcher.isDirectory;
import static com.github.gclaussn.ssg.test.CustomMatcher.isFile;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.github.gclaussn.ssg.Site;

public class DefaultPluginTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder(Paths.get("./target").toFile());

  private Path sitePath;

  private Map<String, Object> properties;

  @Before
  public void setUp() {
    sitePath = temporaryFolder.getRoot().toPath();

    properties = new HashMap<>();
  }

  @Test
  public void shouldInitFromClasspath() {
    Site site = Site.builder().build(sitePath);

    properties.put("ssg.init.template", "classpath:sites/init/default");

    site.getPluginManager().execute("init", properties);
    assertThat(sitePath.resolve(Site.MODEL_NAME), isFile());
    assertThat(site.getSourcePath(), isDirectory());
    assertThat(site.getSourcePath().resolve("index.yaml"), isFile());
    assertThat(site.getPublicPath(), isDirectory());
    assertThat(site.getPublicPath().resolve("index.css"), isFile());
  }

  @Test
  public void shouldInitFromFile() {
    Site site = Site.builder().build(sitePath);

    properties.put("ssg.init.template", "./src/test/resources/sites/init/default");

    site.getPluginManager().execute("init", properties);
    assertThat(sitePath.resolve(Site.MODEL_NAME), isFile());
    assertThat(site.getSourcePath(), isDirectory());
    assertThat(site.getSourcePath().resolve("index.yaml"), isFile());
    assertThat(site.getPublicPath(), isDirectory());
    assertThat(site.getPublicPath().resolve("index.css"), isFile());
  }

  @Test
  public void shouldCopyOutput() throws IOException {
    Site site = Site.builder().build(sitePath);

    properties.put("ssg.init.template", "classpath:sites/init/default");

    site.getPluginManager().execute("init", properties);

    site.load();
    site.generate();

    Path target = Files.createDirectories(site.getPath().resolve("target"));

    properties.put("ssg.cp.target", target.toAbsolutePath().toString());

    site.getPluginManager().execute("cp", properties);
    assertThat(target.resolve("index.html"), isFile());
    assertThat(target.resolve("index.css"), isFile());
    assertThat(target.resolve("node_modules"), isDirectory());
    assertThat(target.resolve("node_modules/test"), isDirectory());
    assertThat(target.resolve("node_modules/test/styles.css"), isFile());
  }
}
