package com.github.gclaussn.ssg.impl;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.PageSet;
import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.SiteException;
import com.github.gclaussn.ssg.SiteGenerator;
import com.github.gclaussn.ssg.SiteGeneratorFn;
import com.github.gclaussn.ssg.data.PageData;
import com.github.gclaussn.ssg.data.PageDataBuilder;
import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.event.SiteEventBuilder;
import com.github.gclaussn.ssg.event.SiteEventType;
import com.github.gclaussn.ssg.impl.markdown.MarkdownFilter;

import de.neuland.jade4j.Jade4J.Mode;
import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.exceptions.JadeException;
import de.neuland.jade4j.exceptions.JadeParserException;
import de.neuland.jade4j.filter.Filter;
import de.neuland.jade4j.template.JadeTemplate;

class SiteGeneratorImpl implements SiteGenerator {

  private final SiteImpl site;

  /** Builtin functions, that are accessible in the JADE model via "_". */
  private final SiteGeneratorFn fn;

  private final JadeConfiguration configuration;

  /** The default page data compiler */
  private final Function<Page, PageData> pageDataCompiler;

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

    pageDataCompiler = new PageDataCompiler();
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
    return pageDataCompiler.apply(site.getPage(pageId));
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

  /**
   * Generates the given page, using the default page data compiler to compile its data.
   * 
   * @param page A specific page.
   * 
   * @return An error, if the compilation or the generation failed. Otherwise an empty optional.
   */
  protected Optional<SiteError> generatePage(Page page) {
    // compile data
    PageData data;
    try {
      data = pageDataCompiler.apply(page);
    } catch (SiteException e) {
      return Optional.of(e.getError());
    }

    return generatePage(page, data);
  }

  /**
   * Generates the given page with the given page data.<br>
   * The method is overloaded to be able to generate a page with data, compiled by a different
   * compiler than the default {@link #pageDataCompiler}.
   * 
   * @param page A specific page.
   * 
   * @param data The compiled data, used for the generation.
   * 
   * @return An error, if the generation failed. Otherwise an empty optional.
   */
  protected Optional<SiteError> generatePage(Page page, PageData data) {
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

    // render page
    SiteError error = null;
    try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
      JadeTemplate template = configuration.getTemplate(page.getTemplateName());
      configuration.renderTemplate(template, data.getRootMap(), writer);
    } catch (JadeException e) {
      error = SiteError.builder(site).source(page).errorPageNotGenerated(e, page.getTemplatePath());
    } catch (IOException e) {
      error = SiteError.builder(site).source(page).errorPageNotGenerated(e);
    } catch (Exception e) {
      // rare case - e.g. script without any content "script."
      String message = String.format("Unexpected Jade lexer/parser error: %s", e.getMessage());
      JadeException exception = new JadeParserException(page.getTemplateName(), -1, configuration.getTemplateLoader(), message);
      error = SiteError.builder(site).source(page).errorPageNotGenerated(exception, null);
    }

    if (error != null) {
      eventBuilder.error(error);
    }

    eventBuilder.buildAndPublish(this::publish);

    return Optional.ofNullable(error);
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

  private void publish(SiteEvent event) {
    site.getConfiguration().onEvent(event);
  }
}
