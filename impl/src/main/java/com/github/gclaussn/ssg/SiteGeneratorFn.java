package com.github.gclaussn.ssg;

public interface SiteGeneratorFn {

  String encodeUriComponent(String component);

  String getPageUrl(String pageId);

  /**
   * Renders the given value as JSON string.
   * 
   * @param value A JSON serializable value.
   * 
   * @return The JSON string.
   */
  String renderJson(Object value);

  String renderMarkdown(String code);
}
