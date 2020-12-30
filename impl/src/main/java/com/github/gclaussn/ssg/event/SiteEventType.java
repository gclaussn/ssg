package com.github.gclaussn.ssg.event;

public enum SiteEventType {

  // Site events
  LOAD_SITE,
  GENERATE_SITE,
  
  // Site file events
  CREATE_YAML,
  CREATE_JADE,
  MODIFY_YAML,
  MODIFY_JADE,
  DELETE_YAML,
  DELETE_JADE,
  
  // Source related events
  LOAD_PAGE,
  LOAD_PAGE_INCLUDE,
  LOAD_PAGE_SET,
  PROCESS_PAGE,
  FILTER_PAGE,
  SELECT_DATA,
  GENERATE_PAGE_SET,
  GENERATE_PAGE
}
