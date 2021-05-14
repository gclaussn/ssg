package com.github.gclaussn.ssg;

import java.net.URLEncoder;

/**
 * Builtin generator functions, accessible via {@link SiteGenerator#FUNCTIONS}.
 */
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

  String linkNodeModule(String relativePath);

  String linkPage(String pageId);

  String linkPublic(String relativePath);

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
