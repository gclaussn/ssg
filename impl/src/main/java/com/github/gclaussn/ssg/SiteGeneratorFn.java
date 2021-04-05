package com.github.gclaussn.ssg;

import java.net.URLEncoder;

public interface SiteGeneratorFn {

  /**
   * Encodes a URI component.
   * 
   * @param component A specific URI component e.g. a query string.
   * 
   * @return The encoded URI component.
   * 
   * @see URLEncoder#encode(String, java.nio.charset.Charset)
   */
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
