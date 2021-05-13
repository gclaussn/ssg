package com.github.gclaussn.ssg.builtin;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.PageProcessor;
import com.github.gclaussn.ssg.Site;

/**
 * Processor, which extracts the date from the model (YAML or Markdown) file name.
 */
public class DateProcessor implements PageProcessor {

  /** Key of the date value e.g. "2020-05-31". */
  public static final String VALUE = "value";

  protected static final String START_TIME = "startTime";
  protected static final String END_TIME = "endTime";
  protected static final String FORMATTED = "formatted";

  protected String dateFormat;
  protected Locale dateFormatLocale;
  protected String timeZoneId;

  private DateTimeFormatter dateTimeFormatter;
  private ZoneId zoneId;

  @Override
  public void init(Site site) {
    dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat, dateFormatLocale);
    zoneId = ZoneId.of(timeZoneId);
  }

  @Override
  public Object process(Page page) {
    Optional<Path> modelPath = page.getModelPath();
    if (modelPath.isEmpty()) {
      return null;
    }

    String fileName = modelPath.get().getFileName().toString();

    LocalDate date = LocalDate.parse(fileName.substring(0, 10));

    Map<String, Object> data = new HashMap<>();
    data.put(START_TIME, date.atStartOfDay(zoneId).toInstant().toEpochMilli());
    data.put(END_TIME, date.plusDays(1L).atStartOfDay(zoneId).toInstant().toEpochMilli());
    data.put(FORMATTED, dateTimeFormatter.format(date));
    data.put(VALUE, date.toString());

    return data;
  }
}
