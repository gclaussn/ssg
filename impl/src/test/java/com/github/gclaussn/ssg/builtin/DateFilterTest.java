package com.github.gclaussn.ssg.builtin;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.data.PageData;

public class DateFilterTest {

  private DateFilter filter;

  @Before
  public void setUp() {
    filter = new DateFilter();
    filter.processorId = "date";
  }

  @Test
  public void testFilter() {
    Map<String, String> data = new HashMap<>();

    Page page = Mockito.mock(Page.class);
    when(page.getData()).thenReturn(PageData.builder().put(filter.processorId, data).build());

    data.put(DateProcessor.VALUE, LocalDate.now().plusDays(1L).toString());
    assertThat(filter.filter(page), is(true));

    data.put(DateProcessor.VALUE, LocalDate.now().toString());
    assertThat(filter.filter(page), is(true));

    data.put(DateProcessor.VALUE, LocalDate.now().minusDays(1L).toString());
    assertThat(filter.filter(page), is(false));

    data.put(DateProcessor.VALUE, null);
    assertThat(filter.filter(page), is(false));

    data.put(DateProcessor.VALUE, "illegal-format");
    try {
      filter.filter(page);
      fail("should throw DateTimeParseException");
    } catch (DateTimeParseException e) {
      assertThat(e.getParsedString(), equalTo(data.get(DateProcessor.VALUE)));
    }
  }
}
