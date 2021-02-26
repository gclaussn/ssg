package com.github.gclaussn.ssg.impl;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import com.github.gclaussn.ssg.Site;

import de.neuland.jade4j.template.TemplateLoader;

/**
 * Custom file based template loader that does not validate the source path premature.
 */
class JadeTemplateLoader implements TemplateLoader {

  private final Site site;

  JadeTemplateLoader(Site site) {
    this.site = site;
  }

  /**
   * Not needed, since caching is disabled.
   */
  @Override
  public long getLastModified(String name) throws IOException {
    return -1L;
  }

  @Override
  public Reader getReader(String name) throws IOException {
    return Files.newBufferedReader(site.getSourcePath().resolve(name), StandardCharsets.UTF_8);
  }

  @Override
  public String getExtension() {
    return "jade";
  }
}
