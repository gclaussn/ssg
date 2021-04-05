package com.github.gclaussn.ssg.impl.npm;

import static com.github.gclaussn.ssg.test.CustomMatcher.isFile;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.ws.rs.NotFoundException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.github.gclaussn.ssg.npm.NodePackageInfo;
import com.github.gclaussn.ssg.npm.NodePackageManager;

public class NodePackageManagerImplTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder(Paths.get("./target").toFile());

  private NodePackageManagerImpl manager;

  @Before
  public void setUp() {
    manager = new NodePackageManagerImpl(NodePackageManager.DEFAULT_REGISTRY_URL);
  }

  @Test
  public void testDownload() throws IOException {
    InputStream in = manager.download("jquery", "3.6.0");
    assertThat(in, notNullValue());

    Path target = temporaryFolder.getRoot().toPath().resolve("jquery-3.6.0.tgz");
    Files.copy(in, target);
    assertThat(target, isFile());
  }

  @Test
  public void testGetPackageVersion() {
    NodePackageInfo nodePackage = manager.getPackage("jquery", "3.6.0");
    assertThat(nodePackage, notNullValue());
    assertThat(nodePackage.getChecksum(), notNullValue());
    assertThat(nodePackage.getFileName(), equalTo("jquery-3.6.0.tgz"));
    assertThat(nodePackage.getName(), equalTo("jquery"));
    assertThat(nodePackage.getUrl(), equalTo("https://registry.npmjs.org/jquery/-/jquery-3.6.0.tgz"));
    assertThat(nodePackage.getVersion(), equalTo("3.6.0"));
  }

  @Test(expected = NotFoundException.class)
  public void testGetPackageVersionNotFound() {
    manager.getPackage("jquery", "0.0.0");
  }
}
