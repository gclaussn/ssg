package com.github.gclaussn.ssg.builtin;

import java.time.LocalDate;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.PageFilter;
import com.github.gclaussn.ssg.data.PageDataNode;
import com.github.gclaussn.ssg.data.PageDataNodeType;

public class DateFilter implements PageFilter {

  /** ID of {@link DateProcessor}, that has provided the data. */
  protected String processorId;

  @Override
  public boolean filter(Page page) {
    PageDataNode node = page.getData().get(processorId);
    if (!node.isMap()) {
      return false;
    }
    
    PageDataNode value = node.get(DateProcessor.VALUE);
    if (!value.is(PageDataNodeType.STRING)) {
      return false;
    }

    LocalDate date = LocalDate.parse(value.as(String.class));

    return !date.isBefore(LocalDate.now());
  }
}
