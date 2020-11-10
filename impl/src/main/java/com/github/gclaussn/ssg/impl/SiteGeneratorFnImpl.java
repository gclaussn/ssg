package com.github.gclaussn.ssg.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteGeneratorFn;

import de.neuland.jade4j.filter.Filter;

class SiteGeneratorFnImpl implements SiteGeneratorFn {

  private final Site site;

  private final Filter markdownFilter;

  private final ObjectMapper objectMapper;

  public SiteGeneratorFnImpl(SiteImpl site, Filter markdownFilter) {
    this.site = site;
    this.markdownFilter = markdownFilter;

    objectMapper = new ObjectMapper();
  }

  @Override
  public String encodeUriComponent(String component) {
    try {
      return URLEncoder.encode(component, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      return null;
    }
  }

  @Override
  public String getPageUrl(String pageId) {
    Objects.requireNonNull(pageId, "page ID is null");

    if (!site.hasPage(pageId)) {
      return null;
    }
    return site.getPage(pageId).getUrl();
  }

  @Override
  public String renderJson(Object value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (IOException e) {
      throw new RuntimeException("JSON could not be rendered", e);
    }
  }

  @Override
  public String renderMarkdown(String source) {
    Objects.requireNonNull(source, "source is null");

    return markdownFilter.convert(source, null, null);
  }
}
