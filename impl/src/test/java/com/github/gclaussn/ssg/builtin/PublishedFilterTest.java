package com.github.gclaussn.ssg.builtin;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.Mockito;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.data.PageData;

public class PublishedFilterTest {

  @Test
  public void testFilter() {
    Page page = Mockito.mock(Page.class);

    when(page.getData()).thenReturn(PageData.builder().put(PublishedFilter.PUBLISHED, Boolean.TRUE).build());
    assertThat(new PublishedFilter().filter(page), is(true));

    when(page.getData()).thenReturn(PageData.builder().put(PublishedFilter.PUBLISHED, Boolean.FALSE).build());
    assertThat(new PublishedFilter().filter(page), is(false));

    when(page.getData()).thenReturn(PageData.builder().put(PublishedFilter.PUBLISHED, null).build());
    assertThat(new PublishedFilter().filter(page), is(true));

    when(page.getData()).thenReturn(PageData.builder().put(PublishedFilter.PUBLISHED, "wrong-type").build());
    assertThat(new PublishedFilter().filter(page), is(false));
  }
}
