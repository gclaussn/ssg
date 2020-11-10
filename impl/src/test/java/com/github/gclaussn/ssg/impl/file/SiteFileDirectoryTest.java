package com.github.gclaussn.ssg.impl.file;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class SiteFileDirectoryTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder(Paths.get("./target").toFile());

  private Path test1;
  private Path test2;

  @Before
  public void setUp() throws IOException {
    test1 = temporaryFolder.newFile("test1").toPath();
    test2 = temporaryFolder.newFile("test2").toPath();
  }

  @Test
  public void testPoll() throws IOException {
    Set<SiteFile> changeSet = new TreeSet<>();

    SiteDirectory directory = new SiteDirectory(temporaryFolder.getRoot().toPath());

    // initialize
    directory.poll(changeSet, true);
    assertThat(changeSet.isEmpty(), is(false));
    assertThat(changeSet.size(), is(2));

    changeSet.clear();

    directory.poll(changeSet, false);
    assertThat(changeSet.isEmpty(), is(true));

    // modify test1
    Files.writeString(test1, "<modified>", StandardCharsets.UTF_8);

    directory.poll(changeSet, false);
    assertThat(changeSet.isEmpty(), is(false));
    assertThat(changeSet.size(), is(1));

    changeSet.clear();

    // delete test2
    Files.delete(test2);

    directory.poll(changeSet, false);
    assertThat(changeSet.isEmpty(), is(false));
    assertThat(changeSet.size(), is(1));

    for (SiteFile siteFile : changeSet) {
      assertThat(siteFile.deleted, is(true));
    }

    changeSet.clear();

    directory.poll(changeSet, true);
    assertThat(changeSet.isEmpty(), is(true));

    // create test3
    Path test3 = temporaryFolder.newFile("test3").toPath();

    directory.poll(changeSet, true);
    assertThat(changeSet.isEmpty(), is(false));
    assertThat(changeSet.size(), is(1));

    for (SiteFile siteFile : changeSet) {
      assertThat(siteFile.path, equalTo(test3));
    }
  }
}
