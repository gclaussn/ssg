package com.github.gclaussn.ssg.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.event.SiteEventStore;
import com.github.gclaussn.ssg.event.SiteEventType;
import com.github.gclaussn.ssg.file.SiteFileEvent;
import com.github.gclaussn.ssg.test.SiteRule;

public class HashCodeBasedPageDataCompilerTest {

  @Rule
  public SiteRule rule = new SiteRule(this);

  @Test
  public void testApply() {
    Site site = rule.init();

    long now = System.currentTimeMillis();
    List<SiteError> errors = site.load();
    assertThat(errors, hasSize(0));

    SiteEventStore eventStore = site.getEventStore();

    List<SiteEvent> events;

    events = eventStore.getEvents("dependent-page", now);
    assertThat(events, hasSize(1));

    SiteFileEvent siteFileEvent = Mockito.mock(SiteFileEvent.class);
    when(siteFileEvent.getPath()).thenReturn(site.getSourcePath().resolve("page-set1/x.yaml"));
    when(siteFileEvent.isSource()).thenReturn(true);

    now = System.currentTimeMillis();
    site.getConfiguration().onEvent(siteFileEvent);
    assertThat(eventStore.getFileEvents(), hasSize(1));

    events = eventStore.getEvents("dependent-page", now);
    assertThat(events, hasSize(2));
    assertThat(events.get(0).getType(), is(SiteEventType.GENERATE_PAGE));
    assertThat(events.get(1).getType(), is(SiteEventType.SELECT_DATA));

    now = System.currentTimeMillis();
    site.getConfiguration().onEvent(siteFileEvent);
    assertThat(eventStore.getFileEvents(), hasSize(2));

    events = eventStore.getEvents("dependent-page", now);
    assertThat(events, hasSize(1));
    assertThat(events.get(0).getType(), is(SiteEventType.SELECT_DATA));

    try {
      Files.writeString(siteFileEvent.getPath(), "data:\n  type: NEW_TYPE\nskip: true", StandardCharsets.UTF_8);
    } catch (IOException e) {
      fail();
    }

    now = System.currentTimeMillis();
    site.getConfiguration().onEvent(siteFileEvent);
    assertThat(eventStore.getFileEvents(), hasSize(3));

    events = eventStore.getEvents("dependent-page", now);
    assertThat(events, hasSize(2));
    assertThat(events.get(0).getType(), is(SiteEventType.GENERATE_PAGE));
    assertThat(events.get(1).getType(), is(SiteEventType.SELECT_DATA));
  }
}
