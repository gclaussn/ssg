package com.github.gclaussn.ssg.impl;

import static com.github.gclaussn.ssg.event.SiteEventType.CREATE_JADE;
import static com.github.gclaussn.ssg.event.SiteEventType.CREATE_YAML;
import static com.github.gclaussn.ssg.event.SiteEventType.DELETE_JADE;
import static com.github.gclaussn.ssg.event.SiteEventType.DELETE_YAML;
import static com.github.gclaussn.ssg.event.SiteEventType.MODIFY_JADE;
import static com.github.gclaussn.ssg.event.SiteEventType.MODIFY_YAML;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.PageInclude;
import com.github.gclaussn.ssg.PageSet;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteGenerator;
import com.github.gclaussn.ssg.SiteOutput;
import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.SourceType;
import com.github.gclaussn.ssg.conf.SiteConf;
import com.github.gclaussn.ssg.error.SiteError;
import com.github.gclaussn.ssg.error.SiteException;
import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.event.SiteEventType;
import com.github.gclaussn.ssg.file.SiteFileEvent;
import com.github.gclaussn.ssg.file.SiteFileEventListener;
import com.github.gclaussn.ssg.file.SiteFileEventType;
import com.github.gclaussn.ssg.file.SiteFileType;
import com.github.gclaussn.ssg.impl.model.SiteModelRepository;
import com.github.gclaussn.ssg.plugin.SitePluginManager;

class SiteImpl implements Site, SiteFileEventListener {

  protected final SiteConfImpl conf;

  protected final SiteModelRepository repository;

  /** Base path of the site. */
  private final Path path;

  private final Path publicPath;
  private final Path sourcePath;
  private final Path outputPath;

  private final SiteGenerator generator;

  SiteImpl(Path sitePath, SiteConfImpl conf) {
    this.conf = conf;

    // initialize repository
    repository = new SiteModelRepository(this);

    path = sitePath;

    publicPath = sitePath.resolve(PUBLIC);
    sourcePath = sitePath.resolve(SOURCE);
    outputPath = sitePath.resolve(OUTPUT);

    // initialize generator
    generator = new SiteGeneratorImpl(this);
  }

  @Override
  public List<Page> analyzePageIncludeUsage(String pageIncludeId) {
    Objects.requireNonNull(pageIncludeId, "page include ID is null");

    PageInclude pageInclude = getPageInclude(pageIncludeId);

    Set<String> pageIncludeIds = getPageIncludes().stream()
        // filter page includes that depend on the given ID
        .filter(value -> value.dependsOn(pageInclude))
        // get ID
        .map(PageInclude::getId)
        // collect unique IDs
        .collect(Collectors.toSet());

    // add the ID of the given page include
    pageIncludeIds.add(pageIncludeId);

    return getPages().stream()
        // filter pages that depend on at least one of the includes
        .filter(page -> !Collections.disjoint(page.getPageIncludeIds(), pageIncludeIds))
        // collect IDs
        .collect(Collectors.toList());
  }

  @Override
  public List<Page> analyzePageSetUsage(String pageSetId) {
    Objects.requireNonNull(pageSetId, "page set ID is null");

    PageSet pageSet = getPageSet(pageSetId);

    return getPages().stream()
        // filter pages that include the given page set
        .filter(page -> page.dependsOn(pageSet))
        // collect pages
        .collect(Collectors.toList());
  }

  @Override
  public void close() {
    conf.pluginManager.preDestroy(this);

    repository.close();
  }

  @Override
  public List<SiteError> generate() {
    return generator.generate();
  }

  @Override
  public SiteConf getConf() {
    return conf;
  }

  @Override
  public SiteGenerator getGenerator() {
    return generator;
  }

  @Override
  public Path getOutputPath() {
    return outputPath;
  }

  @Override
  public Page getPage(String pageId) {
    if (!hasPage(pageId)) {
      throw new SiteException("Page '%s' could not be found", pageId);
    }

    return repository.getPage(pageId);
  }

  @Override
  public Set<Page> getPages() {
    return repository.getPages();
  }

  @Override
  public PageInclude getPageInclude(String pageIncludeId) {
    if (!hasPageInclude(pageIncludeId)) {
      throw new SiteException("Page include '%s' could not be found", pageIncludeId);
    }

    return repository.getPageInclude(pageIncludeId);
  }

  @Override
  public Set<PageInclude> getPageIncludes() {
    return repository.getPageIncludes();
  }

  @Override
  public PageSet getPageSet(String pageSetId) {
    if (!hasPageSet(pageSetId)) {
      throw new SiteException("Page set '%s' could not be found", pageSetId);
    }

    return repository.getPageSet(pageSetId);
  }

  @Override
  public Set<PageSet> getPageSets() {
    return repository.getPageSets();
  }

  @Override
  public Path getPath() {
    return path;
  }

  @Override
  public SitePluginManager getPluginManager() {
    return conf.pluginManager;
  }

  @Override
  public Path getPublicPath() {
    return publicPath;
  }

  @Override
  public Source getSource(String id) {
    return repository.getSource(id);
  }

  @Override
  public Path getSourcePath() {
    return sourcePath;
  }

  @Override
  public boolean hasPage(String pageId) {
    return pageId != null && repository.getPage(pageId) != null;
  }

  @Override
  public boolean hasPageInclude(String pageIncludeId) {
    return pageIncludeId != null && repository.getPageInclude(pageIncludeId) != null;
  }

  @Override
  public boolean hasPageSet(String pageSetId) {
    return pageSetId != null && repository.getPageSet(pageSetId) != null;
  }

  @Override
  public boolean isLoaded() {
    return repository.isLoaded();
  }
  
  @Override
  public List<SiteError> load() {
    // clear stored events
    conf.eventStore.clear();

    return repository.load();
  }

  protected SiteEventType mapEventType(SiteFileEvent fileEvent) {
    switch (fileEvent.getType()) {
      case CREATE:
        return fileEvent.getFileType() == SiteFileType.YAML ? CREATE_YAML : CREATE_JADE;
      case MODIFY:
        return fileEvent.getFileType() == SiteFileType.YAML ? MODIFY_YAML : MODIFY_JADE;
      case DELETE:
        return fileEvent.getFileType() == SiteFileType.YAML ? DELETE_YAML : DELETE_JADE;
      default:
        throw new IllegalArgumentException(String.format("Unsupported file event type %s", fileEvent.getFileType()));
    }
  }

  protected void modify(Source source) {
    String sourceId = source.getId();

    switch (source.getType()) {
      case PAGE:
        modifyPage(sourceId);
        break;
      case PAGE_INCLUDE:
        modifyPageInclude(sourceId);
        break;
      case PAGE_SET:
        modifyPageSet(sourceId);
        break;
      case SITE:
        load();
        break;
      case UNKNOWN:
        // not possible here
    }
  }

  protected void modifyPage(String pageId) {
    Optional<SiteError> error;
    Optional<PageSet> pageSet;

    // load
    pageSet = hasPage(pageId) ? getPage(pageId).getPageSet() : Optional.empty();
    if (pageSet.isPresent()) {
      error = repository.loadPage(pageId, pageSet.get().getId());
    } else {
      error = repository.loadPage(pageId);
    }
    if (error.isPresent()) {
      return;
    }

    // get loaded page
    Page page = getPage(pageId);
    if (page.isRejected()) {
      return;
    }

    // generate
    if (!page.isSkipped()) {
      error = page.generate();
    }
    if (!page.isSkipped() && error.isPresent()) {
      return;
    }

    pageSet = page.getPageSet();
    if (!pageSet.isPresent()) {
      // no other page is affected
      return;
    }

    String pageSetId = pageSet.get().getId();

    analyzePageSetUsage(pageSetId).stream().filter(Page::isGenerated).forEach(Page::generate);
  }

  protected void modifyPageInclude(String pageIncludeId) {
    Queue<String> queue = new LinkedList<>(Collections.singletonList(pageIncludeId));

    List<SiteError> errors = repository.loadPageIncludes(queue);
    if (!errors.isEmpty()) {
      return;
    }

    analyzePageIncludeUsage(pageIncludeId).stream().filter(Page::isGenerated).forEach(Page::generate);
  }

  protected void modifyPageSet(String pageSetId) {
    List<SiteError> errors;

    // load
    errors = repository.loadPageSet(pageSetId);
    if (!errors.isEmpty()) {
      return;
    }

    PageSet pageSet = getPageSet(pageSetId);

    // generate
    if (!pageSet.isSkipped()) {
      errors = pageSet.generate();
    }
    if (!pageSet.isSkipped() && !errors.isEmpty()) {
      return;
    }

    analyzePageSetUsage(pageSetId).stream().filter(Page::isGenerated).forEach(Page::generate);
  }

  @Override
  public void onEvent(SiteFileEvent event) {
    switch (event.getFileType()) {
      case YAML:
      case JADE:
        // are supported
        break;
      default:
        // ignore events with file types other than YAML or JADE
        return;
    }

    conf.eventStore.onEvent(event);

    // try to determine, what source (site, page, page include, page set) is affected
    Source source = repository.getSource(event.getPath());

    if (source.getType() == SourceType.UNKNOWN) {
      // ignore unknown source types
      return;
    }

    SiteEvent.builder()
        .type(mapEventType(event))
        .source(source)
        .reference(event.getPath().toString())
        .buildAndPublish(conf::publish);

    if (source.getType() == SourceType.SITE && event.getType() == SiteFileEventType.DELETE) {
      // ignore deletion of site.yaml
      return;
    }

    modify(source);
  }

  @Override
  public Stream<SiteOutput> serve() {
    return new SiteOutputServer(this).serve();
  }

  @Override
  public SiteOutput serve(String requestUri) {
    Objects.requireNonNull(requestUri, "request URI is null");

    return new SiteOutputServer(this).serve(requestUri);
  }
}
