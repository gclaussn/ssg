package com.github.gclaussn.ssg.impl;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.PageInclude;
import com.github.gclaussn.ssg.PageSet;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.SiteException;
import com.github.gclaussn.ssg.SiteGenerator;
import com.github.gclaussn.ssg.SiteModelApi;
import com.github.gclaussn.ssg.SiteOutput;
import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.conf.SiteConf;
import com.github.gclaussn.ssg.event.SiteEventStore;
import com.github.gclaussn.ssg.impl.conf.SiteConfImpl;
import com.github.gclaussn.ssg.impl.event.SiteEventStoreImpl;
import com.github.gclaussn.ssg.impl.model.SiteModelRepository;
import com.github.gclaussn.ssg.impl.plugin.SitePluginManagerImpl;
import com.github.gclaussn.ssg.npm.NodePackageSpec;
import com.github.gclaussn.ssg.plugin.SitePluginManager;

class SiteImpl implements Site {

  protected final SiteConfImpl conf;
  protected final SiteEventStoreImpl eventStore;
  protected final SitePluginManagerImpl pluginManager;

  protected final SiteModelRepository repository;
  protected final SiteModelApi modelApi;
  protected final SiteGeneratorImpl generator;

  /** Base path of the site. */
  private final Path path;

  private final Path publicPath;
  private final Path sourcePath;
  private final Path outputPath;

  SiteImpl(SiteBuilderImpl builder, Path sitePath) {
    this.conf = builder.conf;
    this.eventStore = builder.eventStore;
    this.pluginManager = builder.pluginManager;

    path = sitePath;

    publicPath = sitePath.resolve(PUBLIC);
    sourcePath = sitePath.resolve(SOURCE);
    outputPath = sitePath.resolve(OUTPUT);

    // initialize components
    repository = new SiteModelRepository(this);
    modelApi = repository.createModelApi();
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
    pluginManager.preDestroy(this);

    repository.close();
  }

  @Override
  public List<SiteError> generate() {
    return generator.generate();
  }

  @Override
  public SiteConf getConfiguration() {
    return conf;
  }

  @Override
  public SiteEventStore getEventStore() {
    return eventStore;
  }

  @Override
  public SiteGenerator getGenerator() {
    return generator;
  }

  @Override
  public SiteModelApi getModelApi() {
    return modelApi;
  }

  @Override
  public Optional<NodePackageSpec> getNodePackageSpec() {
    return repository.getNodePackageSpec();
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
    return pluginManager;
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
  public List<SiteError> load() {
    // clear stored events
    eventStore.clear();

    return repository.load();
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
