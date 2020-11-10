package com.github.gclaussn.ssg.builtin;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.github.gclaussn.ssg.Page;

public class DateProcessorTest {

  private DateProcessor processor;

  @Before
  public void setUp() {
    processor = new DateProcessor();
    processor.dateFormat = "dd.MM.yyyy";
    processor.dateFormatLocale = Locale.forLanguageTag("de");
    processor.timeZoneId = "CET";

    processor.init(null);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testProcess() {
    Page page = Mockito.mock(Page.class);
    when(page.getModelPath()).thenReturn(Optional.of(Paths.get("/test/2020-07-26-xyz")));

    LocalDate expected = LocalDate.of(2020, Month.JULY, 26);
    ZoneId expectedZoneId = ZoneId.of(processor.timeZoneId);
    
    Map<String, Object> data = (Map<String, Object>) processor.process(page);
    assertThat(data.get(DateProcessor.VALUE), equalTo(expected.toString()));
    assertThat(data.get(DateProcessor.FORMATTED), equalTo("26.07.2020"));
    
    long startTime = expected.atStartOfDay(expectedZoneId).toInstant().toEpochMilli();
    assertThat(data.get(DateProcessor.START_TIME), is(startTime));
    
    long endTime = expected.plusDays(1L).atStartOfDay(expectedZoneId).toInstant().toEpochMilli();
    assertThat(data.get(DateProcessor.END_TIME), is(endTime));
  }
}
