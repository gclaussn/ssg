package com.github.gclaussn.ssg.server.file;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.file.SiteFileEvent;
import com.github.gclaussn.ssg.file.SiteFileEventListener;

public class PollingFileWatcherTest implements SiteFileEventListener {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder(Paths.get("./target").toFile());

  private List<SiteFileEvent> events = new LinkedList<>();

  private Site site;

  @Before
  public void setUp() throws IOException {
    temporaryFolder.newFile(Site.MODEL_NAME);
    temporaryFolder.newFolder(Site.SOURCE);

    site = Site.from(temporaryFolder.getRoot().toPath());
  }

  @Override
  public void onEvent(SiteFileEvent event) {
    events.add(event);
  }

  @After
  public void tearDown() {
    events.clear();
  }

  @Test
  public void testFactory() throws IOException {
    SiteFileWatcher watcher = SiteFileWatcher.of(site, SiteFileWatcherType.POLLING);
    assertThat(watcher.getType(), is(SiteFileWatcherType.POLLING));

    watcher.start(this);
    watcher.stop();
  }

  @Test
  public void testWatch() throws IOException {
    PollingFileWatcher watcher = new PollingFileWatcher();
    watcher.site = site;
    watcher.eventListeners.add(this);

    watcher.runInitially();
    watcher.run();

    assertThat(events.isEmpty(), is(true));

    Path testPath = Files.createDirectory(site.getSourcePath().resolve("test"));
    Path test1 = Files.createFile(testPath.resolve("1.jade"));

    watcher.run();
    watcher.run();

    assertThat(events.isEmpty(), is(false));
    assertThat(events.get(0).getPath(), equalTo(test1));
  }
}
