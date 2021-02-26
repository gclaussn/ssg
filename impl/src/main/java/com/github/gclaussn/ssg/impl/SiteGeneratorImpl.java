package com.github.gclaussn.ssg.impl;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.PageInclude;
import com.github.gclaussn.ssg.PageSet;
import com.github.gclaussn.ssg.SiteGenerator;
import com.github.gclaussn.ssg.SiteGeneratorFn;
import com.github.gclaussn.ssg.data.PageData;
import com.github.gclaussn.ssg.data.PageDataBuilder;
import com.github.gclaussn.ssg.data.PageDataSelectorBean;
import com.github.gclaussn.ssg.error.SiteError;
import com.github.gclaussn.ssg.error.SiteException;
import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.event.SiteEventBuilder;
import com.github.gclaussn.ssg.event.SiteEventType;
import com.github.gclaussn.ssg.impl.markdown.MarkdownFilter;

import de.neuland.jade4j.Jade4J.Mode;
import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.exceptions.JadeException;
import de.neuland.jade4j.filter.Filter;
import de.neuland.jade4j.template.JadeTemplate;

class SiteGeneratorImpl implements SiteGenerator {

  private final SiteImpl site;

  /** Builtin functions, that are accessible in the JADE model via "_fn". */
  private final SiteGeneratorFn fn;

  private final JadeConfiguration configuration;

  SiteGeneratorImpl(SiteImpl site) {
    this.site = site;

    Filter markdownFilter = new MarkdownFilter(site);

    configuration = new JadeConfiguration();
    configuration.setCaching(false);
    configuration.setMode(Mode.XHTML);
    configuration.setPrettyPrint(true);
    configuration.setTemplateLoader(new JadeTemplateLoader(site));

    fn = new SiteGeneratorFnImpl(site, markdownFilter);
    configuration.getSharedVariables().put(SiteGenerator.FUNCTIONS, fn);

    PageData extensions = compileExtensions(site.conf.getExtensions());
    configuration.getSharedVariables().put(SiteGenerator.EXTENSIONS, extensions.getRootMap());

    configuration.getFilters().put("markdown", markdownFilter);
    configuration.getFilters().put("md", markdownFilter);
  }

  protected Map<String, Object> buildMetadata(Page page) {
    Map<String, Object> meta = new HashMap<>();
    meta.put("id", page.getId());
    meta.put("subId", page.getSubId().orElse(null));
    meta.put("url", page.getUrl());

    return meta;
  }

  protected PageData compileExtensions(Set<Object> extensions) {
    PageDataBuilder builder = PageData.builder();

    for (Object extension : extensions) {
      String typeName = extension.getClass().getName();
      builder.put(typeName.replace('.', '/'), extension);
    }

    return builder.build();
  }

  @Override
  public PageData compilePageData(String pageId) {
    return compilePageData(site.getPage(pageId));
  }

  protected PageData compilePageData(Page page) {
    PageDataBuilder builder = PageData.builder();

    // put page data
    builder.putRoot(page.getData());

    builder.put(PageData.META, buildMetadata(page));

    // put data from page includes
    for (PageInclude pageInclude : resolvePageIncludes(page)) {
      String id = normalizeId(pageInclude.getId());
      builder.put(id, pageInclude.getData().getRootMap());
    }

    // put data from selectors
    for (PageDataSelectorBean dataSelector : page.getDataSelectors()) {
      String id = normalizeId(dataSelector.getId());
      builder.putIfAbsent(id, dataSelector.select(page));
    }

    return builder.build();
  }

  protected void createOutputDirectory() {
    try {
      FileUtils.forceMkdir(site.getOutputPath().toFile());
    } catch (IOException e) {
      throw SiteError.builder(site).errorOutputDirectoryNotCreated(e).toException();
    }
  }

  protected void deleteOutputDirectory() {
    if (!Files.exists(site.getOutputPath())) {
      return;
    }

    try {
      FileUtils.deleteDirectory(site.getOutputPath().toFile());
    } catch (IOException e) {
      throw SiteError.builder(site).errorOutputDirectoryNotDeleted(e).toException();
    }
  }

  private boolean filterPage(Page page) {
    return !page.isSkipped() && !page.isRejected();
  }

  private boolean filterPageNoSet(Page page) {
    return page.getPageSet().isEmpty();
  }

  @Override
  public List<SiteError> generate() {
    SiteEventBuilder eventBuilder = SiteEvent.builder().type(SiteEventType.GENERATE_SITE);

    try {
      deleteOutputDirectory();
      createOutputDirectory();
    } catch (SiteException e) {
      eventBuilder.error(e.getError());
      return Collections.singletonList(e.getError());
    } finally {
      publish(eventBuilder.build());
    }

    List<SiteError> errors = new LinkedList<>();

    if (!site.isLoaded()) {
      return errors;
    }

    // generate pages, that are not part of a page set
    site.getPages().stream()
        // filter pages, that has no page set
        .filter(this::filterPageNoSet)
        // filter pages, that are neither skipped nor rejected
        .filter(this::filterPage)
        // generate page
        .map(this::generatePage)
        // filter errors
        .filter(Optional::isPresent)
        // get error
        .map(Optional::get)
        // collect errors
        .forEach(errors::add);

    // generate page sets
    site.getPageSets().stream()
        // generate page set
        .map(this::generatePageSet)
        // collect errors
        .forEach(errors::addAll);

    return errors;
  }

  @Override
  public Optional<SiteError> generatePage(String pageId) {
    Page page = site.getPage(pageId);

    if (page.isSkipped()) {
      throw new SiteException("Page '%s' cannot be generated: page is skipped", pageId);
    }
    if (page.isRejected()) {
      throw new SiteException("Page '%s' cannot be generated: page is rejected", pageId);
    }

    return generatePage(page);
  }

  protected Optional<SiteError> generatePage(Page page) {
    SiteEventBuilder eventBuilder = SiteEvent.builder().type(SiteEventType.GENERATE_PAGE).source(page);

    Path path = page.getOutputPath();

    // create parent directories
    try {
      Files.createDirectories(path.getParent());
    } catch (IOException e) {
      SiteError error = SiteError.builder(site).source(page).errorPageOutputDirectoryNotCreated(e);
      eventBuilder.error(error).buildAndPublish(this::publish);
      return Optional.of(error);
    }

    // compile data
    PageData data;
    try {
      data = compilePageData(page);
    } catch (SiteException e) {
      return Optional.of(e.getError());
    }

    // render page
    try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
      JadeTemplate template = configuration.getTemplate(page.getTemplateName());
      configuration.renderTemplate(template, data.getRootMap(), writer);
    } catch (JadeException e) {
      SiteError error = SiteError.builder(site).source(page).errorPageNotGenerated(e);
      eventBuilder.error(error);
      return Optional.of(error);
    } catch (IOException e) {
      SiteError error = SiteError.builder(site).source(page).errorPageNotGenerated(e);
      eventBuilder.error(error);
      return Optional.of(error);
    } finally {
      publish(eventBuilder.build());
    }

    return Optional.empty();
  }

  @Override
  public List<SiteError> generatePageSet(String pageSetId) {
    PageSet pageSet = site.getPageSet(pageSetId);

    if (pageSet.isSkipped()) {
      throw new SiteException("Page set '%s' cannot be generated: page set is skipped", pageSetId);
    }

    return generatePageSet(pageSet);
  }

  protected List<SiteError> generatePageSet(PageSet pageSet) {
    if (pageSet.isSkipped()) {
      return Collections.emptyList();
    }

    SiteEvent.builder().type(SiteEventType.GENERATE_PAGE_SET).source(pageSet).buildAndPublish(this::publish);

    List<SiteError> errors = pageSet.getPages().stream()
        // filter pages, that are neither skipped nor rejected
        .filter(this::filterPage)
        // generate page
        .map(this::generatePage)
        // filter errors
        .filter(Optional::isPresent)
        // get error
        .map(Optional::get)
        // collect errors
        .collect(Collectors.toList());

    return errors;
  }

  @Override
  public SiteGeneratorFn getFunctions() {
    return fn;
  }

  protected Set<PageInclude> resolvePageIncludes(Page page) {
    Queue<PageInclude> queue = new LinkedList<>();
    for (PageInclude pageInclude : page.getPageIncludes()) {
      queue.add(pageInclude);
    }

    Set<PageInclude> pageIncludes = new HashSet<>();
    while (!queue.isEmpty()) {
      PageInclude pageInclude = queue.poll();
      if (pageIncludes.contains(pageInclude)) {
        continue;
      }

      pageIncludes.add(pageInclude);
      pageInclude.getPageIncludes().stream().forEach(queue::add);
    }

    return pageIncludes;
  }

  protected String normalizeId(String id) {
    StringBuilder sb = new StringBuilder(id.length());

    boolean toUpper = false;
    for (int i = 0; i < id.length(); i++) {
      char c = id.charAt(i);

      if (c == '-' || c == '_') {
        toUpper = true;
      } else if (toUpper) {
        sb.append(Character.toUpperCase(c));
        toUpper = false;
      } else {
        sb.append(c);
      }
    }

    return sb.toString();
  }

  protected void publish(SiteEvent event) {
    site.getConf().publish(event);
  }
}
