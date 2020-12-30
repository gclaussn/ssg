package com.github.gclaussn.ssg.impl;

import static com.github.gclaussn.ssg.file.SiteFileType.HTML;
import static com.github.gclaussn.ssg.file.SiteFileType.JADE;
import static com.github.gclaussn.ssg.file.SiteFileType.YAML;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.PageFilterBean;
import com.github.gclaussn.ssg.PageInclude;
import com.github.gclaussn.ssg.PageProcessorBean;
import com.github.gclaussn.ssg.PageSet;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.SiteException;
import com.github.gclaussn.ssg.SiteGenerator;
import com.github.gclaussn.ssg.SiteOutput;
import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.SourceType;
import com.github.gclaussn.ssg.conf.SiteConf;
import com.github.gclaussn.ssg.data.PageData;
import com.github.gclaussn.ssg.data.PageDataBuilder;
import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.plugin.SitePluginManager;

class SiteImpl implements Site {

  protected final SiteConfImpl conf;

  protected final SiteErrorFactory errorFactory;
  protected final SiteEventFactory eventFactory;

  protected final Map<String, PageImpl> pages;
  protected final Map<String, PageIncludeImpl> pageIncludes;
  protected final Map<String, PageSetImpl> pageSets;

  protected SiteModel model;

  /** Base path of the site. */
  private final Path path;

  private final Path publicPath;
  private final Path sourcePath;
  private final Path outputPath;

  private final SiteGenerator generator;

  SiteImpl(Path sitePath, SiteConfImpl conf) {
    this.conf = conf;

    errorFactory = new SiteErrorFactory(this);
    eventFactory = new SiteEventFactory();

    path = sitePath;

    publicPath = sitePath.resolve(PUBLIC);
    sourcePath = sitePath.resolve(SOURCE);
    outputPath = sitePath.resolve(OUTPUT);

    pages = createMap(PageImpl.class);
    pageIncludes = createMap(PageIncludeImpl.class);
    pageSets = createMap(PageSetImpl.class);

    // initialize generator
    generator = new SiteGeneratorImpl(this);
  }

  @Override
  public List<Page> analyzePageIncludeUsage(String pageIncludeId) {
    Objects.requireNonNull(pageIncludeId, "page include ID is null");

    PageInclude pageInclude = getPageInclude(pageIncludeId);

    Set<String> pageIncludeIds = pageIncludes.values().stream()
        // filter page includes that depend on the given ID
        .filter(value -> value.dependsOn(pageInclude))
        // get ID
        .map(PageInclude::getId)
        // collect unique IDs
        .collect(Collectors.toSet());

    // add the ID of the given page include
    pageIncludeIds.add(pageIncludeId);

    return pages.values().stream()
        // filter pages that depend on at least one of the includes
        .filter(page -> !Collections.disjoint(page.model.includes, pageIncludeIds))
        // collect IDs
        .collect(Collectors.toList());
  }

  @Override
  public List<Page> analyzePageSetUsage(String pageSetId) {
    Objects.requireNonNull(pageSetId, "page set ID is null");

    PageSet pageSet = getPageSet(pageSetId);

    return pages.values().stream()
        // filter pages that include the given page set
        .filter(page -> page.dependsOn(pageSet))
        // collect pages
        .collect(Collectors.toList());
  }

  protected Map<String, Object> buildMetadata(Page page) {
    Map<String, Object> meta = new HashMap<>();
    meta.put("id", page.getId());
    meta.put("subId", page.getSubId().orElse(null));
    meta.put("url", page.getUrl());

    return meta;
  }

  /**
   * Builds the output name for a given page that is part of a page set.<br />
   * The output name is built using the base path of the page set and the page ID.
   * 
   * Examples:
   * 
   * <pre>
   * basePath | pageId                | outputName
   * null     | posts/my-post         | posts/my-post.html
   * news     | posts/my-post         | news/my-post.html
   * news     | posts/2020/05/my-post | news/2020/05/my-post.html
   * </pre>
   * 
   * @param pageId A specific page ID.
   * 
   * @param pageSet The related page set.
   * 
   * @return The output name, including the HTML file extension.
   */
  protected String buildOutputName(String pageId, PageSet pageSet) {
    Optional<String> basePath = pageSet.getBasePath();
    if (!basePath.isPresent()) {
      return HTML.appendTo(pageId);
    }

    String subId = extractSubId(pageSet.getId(), pageId);

    return new StringBuilder(basePath.get().length() + subId.length() + 6)
        .append(basePath.get())
        .append('/')
        .append(HTML.appendTo(subId))
        .toString();
  }

  protected String buildUrl(PageModel model) {
    String outputName;
    if (model.outputName.equals("index.html")) {
      outputName = StringUtils.EMPTY;
    } else if (HTML.isPresent(model.outputName)) {
      outputName = HTML.strip(model.outputName);
    } else {
      outputName = model.outputName;
    }

    return new StringBuilder(outputName.length() + 1)
        .append('/')
        .append(outputName)
        .toString();
  }

  @Override
  public void close() {
    conf.pluginManager.preDestroy(this);

    reset();
  }

  protected List<String> collectPageIds(String pageSetId) {
    Path path = sourcePath.resolve(pageSetId);

    List<String> pageIds;
    try {
      pageIds = Files.walk(path, Integer.MAX_VALUE)
          // filter regular files
          .filter(Files::isRegularFile)
          // filter YAML files
          .filter(YAML::isPresent)
          // extract ID from file path
          .map(this::extractId)
          // collect IDs
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw errorFactory.pageSetNotTraversed(e, new SourceImpl(SourceType.PAGE_SET, pageSetId)).toException();
    }

    return pageIds;
  }

  protected <V> Map<String, V> createMap(Class<V> valueType) {
    return new LinkedHashMap<>();
  }

  protected SiteOutputServer createOutputServer() {
    if (isLoaded()) {
      return new SiteOutputServer(this);
    } else {
      // load site model
      return new SiteOutputServer(this, readSiteModel(path.resolve(MODEL_NAME)));
    }
  }

  /**
   * Extracts the ID of a page, a page set or a page include from a given path within the source
   * folder - e.g. "/site/src/events/2020-04-25.yaml" -> events/2020-04-25
   * 
   * @param path Path of a YAML or JADE file within the source folder.
   * 
   * @return The extracted ID.
   */
  protected String extractId(Path path) {
    String relativePath = sourcePath.relativize(path).toString();

    // -5: the length of .yaml or .jade file extensions
    String relativePathWithoutExtension = relativePath.substring(0, relativePath.length() - 5);

    if (relativePathWithoutExtension.startsWith("..")) {
      // in case of site.yaml
      return null;
    }

    // ensure unix file separator
    int index = relativePathWithoutExtension.indexOf('\\');
    if (index >= 0) {
      return relativePathWithoutExtension.replace('\\', '/');
    } else {
      return relativePathWithoutExtension;
    }
  }

  protected String extractSubId(String pageSetId, String pageId) {
    return pageId.substring(pageSetId.length() + 1, pageId.length());
  }

  protected Optional<SiteError> filterPage(PageImpl page) {
    PageSetImpl pageSet = pageSets.get(page.setId);

    for (PageFilterBean filter : pageSet.model.filters) {
      try {
        if (!filter.filter(page)) {
          page.rejectedBy = filter.getId();
          break;
        }
      } catch (SiteException e) {
        return Optional.of(e.getError());
      }
    }

    return Optional.empty();
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
      throw new SiteException("page '%s' could not be found", pageId);
    }

    return pages.get(pageId);
  }

  @Override
  public PageInclude getPageInclude(String pageIncludeId) {
    if (!hasPageInclude(pageIncludeId)) {
      throw new SiteException("page include '%s' could not be found", pageIncludeId);
    }

    return pageIncludes.get(pageIncludeId);
  }

  @Override
  public Set<Page> getPages() {
    return new HashSet<>(pages.values());
  }

  @Override
  public PageSet getPageSet(String pageSetId) {
    if (!hasPageSet(pageSetId)) {
      throw new SiteException("page set '%s' could not be found", pageSetId);
    }

    return pageSets.get(pageSetId);
  }

  @Override
  public Set<PageSet> getPageSets() {
    return new HashSet<>(pageSets.values());
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
    return new SourceImpl(getSourceType(id), id);
  }

  @Override
  public Path getSourcePath() {
    return sourcePath;
  }

  /**
   * Determines the type of the source with the given ID. The source type is determined by finding
   * the ID within all known pages, page sets and page inclundes.
   * 
   * @param id A specific source ID.
   * 
   * @return The source type or {@link SourceType#UNKNOWN}, if the given source is not known.
   */
  protected SourceType getSourceType(String id) {
    if (id == null) {
      return SourceType.SITE;
    } else if (pages.containsKey(id)) {
      return SourceType.PAGE;
    } else if (pageIncludes.containsKey(id)) {
      return SourceType.PAGE_INCLUDE;
    } else if (pageSets.containsKey(id)) {
      return SourceType.PAGE_SET;
    } else {
      return SourceType.UNKNOWN;
    }
  }

  @Override
  public boolean hasPage(String pageId) {
    return pageId != null && pages.containsKey(pageId);
  }

  @Override
  public boolean hasPageInclude(String pageIncludeId) {
    return pageIncludeId != null && pageIncludes.containsKey(pageIncludeId);
  }

  @Override
  public boolean hasPageSet(String pageSetId) {
    return pageSetId != null && pageSets.containsKey(pageSetId);
  }

  @Override
  public boolean isLoaded() {
    return model != null;
  }

  @Override
  public List<SiteError> load() {
    SiteEvent event = eventFactory.loadSite();

    reset();

    // load site model
    try {
      model = readSiteModel(path.resolve(MODEL_NAME));
    } catch (SiteException e) {
      model = null;

      event.with(e.getError()).publish(this::publishEvent);
      return Collections.singletonList(e.getError());
    }

    model.nodeModules = orElse(model.nodeModules, NodeModules::new);
    model.pages = orElse(model.pages, Collections::emptySet);
    model.pageSets = orElse(model.pageSets, Collections::emptySet);

    publishEvent(event);

    List<SiteError> errors = new LinkedList<>();

    // load pages
    model.pages.stream().map(this::loadPage).filter(Optional::isPresent).map(Optional::get).forEach(errors::add);

    // load page sets
    model.pageSets.stream().map(this::loadPageSet).forEach(errors::addAll);

    Queue<String> includeQueue = new LinkedList<>();
    for (PageImpl page : pages.values()) {
      includeQueue.addAll(page.model.includes);
    }

    // load page includes
    errors.addAll(loadPageIncludes(includeQueue));

    return errors;
  }


  protected Optional<SiteError> loadPage(String pageId) {
    SiteEvent event = eventFactory.loadPage(new SourceImpl(SourceType.PAGE, pageId));

    // load model
    PageModel model;
    try {
      model = readPageModel(pageId);
    } catch (SiteException e) {
      event.with(e.getError()).publish(this::publishEvent);
      return Optional.of(e.getError());
    }
    
    model.data = orElse(model.data, PageData::empty);
    model.dataSelectors = orElse(model.dataSelectors, Collections::emptyList);
    model.includes = orElse(model.includes, Collections::emptySet);
    model.outputName = orElse(model.outputName, HTML.appendTo(pageId));
    model.skip = orElse(model.skip, Boolean.FALSE);

    PageImpl page = new PageImpl(this, model);
    page.id = pageId;
    page.templateName = JADE.appendTo(pageId);
    page.url = buildUrl(model);

    try {
      page.init();
    } catch (SiteException e) {
      event.with(e.getError()).publish(this::publishEvent);
      return Optional.of(e.getError());
    }

    // put metadata
    page.model.data = PageData.builder()
        .putRoot(page.model.data)
        .put(PageData.META, buildMetadata(page))
        .build();

    publishEvent(event);
    pages.put(pageId, page);

    return Optional.empty();
  }

  protected Optional<SiteError> loadPage(String pageId, PageSetImpl pageSet) {
    SiteEvent event = eventFactory.loadPage(new SourceImpl(SourceType.PAGE, pageId), pageSet.id);

    // load model
    PageModel model;
    try {
      model = readPageModel(pageId);
    } catch (SiteException e) {
      event.with(e.getError()).publish(this::publishEvent);
      return Optional.of(e.getError());
    }

    model.data = orElse(model.data, PageData::empty);
    model.dataSelectors = orElse(model.dataSelectors, pageSet.model.dataSelectors);
    model.includes = orElse(model.includes, pageSet.model.includes);
    model.outputName = orElse(model.outputName, buildOutputName(pageId, pageSet));
    model.skip = orElse(model.skip, pageSet.isSkipped());

    PageImpl page = new PageImpl(this, model);
    page.id = pageId;
    page.setId = pageSet.getId();
    page.subId = extractSubId(pageSet.getId(), pageId);
    page.templateName = JADE.appendTo(pageId);
    page.url = buildUrl(model);

    try {
      page.init();
    } catch (SiteException e) {
      event.with(e.getError()).publish(this::publishEvent);
      return Optional.of(e.getError());
    }

    Path templatePath = sourcePath.resolve(JADE.appendTo(pageId));
    if (!Files.exists(templatePath)) {
      page.templateName = pageSet.getTemplateName();
    }
    
    publishEvent(event);

    PageDataBuilder dataBuilder = PageData.builder()
        .putRoot(page.model.data)
        .put(PageData.META, buildMetadata(page));

    // process page
    Optional<SiteError> processError = processPage(page, dataBuilder);
    if (processError.isPresent()) {
      return processError;
    }

    page.model.data = dataBuilder.build();

    // filter page
    Optional<SiteError> filterError = filterPage(page);
    if (filterError.isPresent()) {
      return filterError;
    }

    pages.put(pageId, page);

    return Optional.empty();
  }

  protected Optional<SiteError> loadPageInclude(String pageIncludeId) {
    SiteEvent event = eventFactory.loadPageInclude(new SourceImpl(SourceType.PAGE_INCLUDE, pageIncludeId));

    // load model
    PageIncludeModel model;
    try {
      model = readPageIncludeModel(pageIncludeId);
    } catch (SiteException e) {
      event.with(e.getError()).publish(this::publishEvent);
      return Optional.of(e.getError());
    }

    model.data = orElse(model.data, PageData::empty);
    model.includes = orElse(model.includes, Collections::emptySet);

    PageIncludeImpl pageInclude = new PageIncludeImpl(this, model);
    pageInclude.id = pageIncludeId;

    String templateName = JADE.appendTo(pageIncludeId);
    if (Files.exists(sourcePath.resolve(templateName))) {
      pageInclude.templateName = templateName;
    }

    publishEvent(event);
    pageIncludes.put(pageIncludeId, pageInclude);

    return Optional.empty();
  }

  protected List<SiteError> loadPageIncludes(Queue<String> queue) {
    List<SiteError> errors = new LinkedList<>();

    while (!queue.isEmpty()) {
      String id = queue.poll();

      SourceType sourceType = getSourceType(id);

      if (sourceType != SourceType.UNKNOWN) {
        continue;
      }

      Optional<SiteError> error = loadPageInclude(id);
      if (error.isPresent()) {
        errors.add(error.get());
        continue;
      }

      PageIncludeImpl pageInclude = pageIncludes.get(id);
      queue.addAll(pageInclude.model.includes);
    }

    return errors;
  }

  protected List<SiteError> loadPageSet(String pageSetId) {
    SiteEvent event = eventFactory.loadPageSet(new SourceImpl(SourceType.PAGE_SET, pageSetId));

    // load model
    PageSetModel model;
    try {
      model = readPageSetModel(pageSetId);
    } catch (SiteException e) {
      event.with(e.getError()).publish(this::publishEvent);
      return Collections.singletonList(e.getError());
    }

    model.data = orElse(model.data, PageData::empty);
    model.dataSelectors = orElse(model.dataSelectors, Collections::emptyList);
    model.filters = orElse(model.filters, Collections::emptyList);
    model.includes = orElse(model.includes, Collections::emptySet);
    model.processors = orElse(model.processors, Collections::emptyList);
    model.skip = orElse(model.skip, Boolean.FALSE);

    PageSetImpl pageSet = new PageSetImpl(this, model);
    pageSet.id = pageSetId;
    pageSet.pages = new LinkedHashSet<>();
    pageSet.templateName = JADE.appendTo(pageSetId);

    try {
      pageSet.init();
    } catch (SiteException e) {
      event.with(e.getError()).publish(this::publishEvent);
      return Collections.singletonList(e.getError());
    }

    List<String> pageIds;
    try {
      pageIds = collectPageIds(pageSetId);
    } catch (SiteException e) {
      event.with(e.getError()).publish(this::publishEvent);
      return Collections.singletonList(e.getError());
    }

    publishEvent(event);
    pageSets.put(pageSetId, pageSet);


    List<SiteError> errors = new LinkedList<>();
    for (String pageId : pageIds) {
      Optional<SiteError> error = loadPage(pageId, pageSet);
      if (error.isPresent()) {
        errors.add(error.get());
      } else {
        pageSet.pages.add(pageId);
      }
    }

    return errors;
  }

  private <T> T orElse(T value, Supplier<T> defaultValueSupplier) {
    return value != null ? value : defaultValueSupplier.get();
  }

  private <T> T orElse(T value, T defaultValue) {
    return value != null ? value : defaultValue;
  }
  
  protected Optional<SiteError> processPage(PageImpl page, PageDataBuilder dataBuilder) {
    PageSetImpl pageSet = pageSets.get(page.setId);

    for (PageProcessorBean processor : pageSet.model.processors) {
      Object processed;
      try {
        processed = processor.process(page);
      } catch (SiteException e) {
        return Optional.of(e.getError());
      }

      dataBuilder.put(processor.getId(), processed);
    }

    return Optional.empty();
  }

  protected void publishEvent(SiteEvent event) {
    conf.getEventListeners().forEach(l -> l.onEvent(event));
  }

  protected PageIncludeModel readPageIncludeModel(String pageIncludeId) {
    Path filePath = sourcePath.resolve(YAML.appendTo(pageIncludeId));

    if (!Files.exists(filePath)) {
      return new PageIncludeModel();
    }

    PageIncludeModel model;
    try {
      model = conf.objectMapper.readValue(filePath.toFile(), PageIncludeModel.class);
      model.filePath = filePath;
    } catch (IOException e) {
      throw errorFactory.modelNotRead(e, new SourceImpl(SourceType.PAGE_INCLUDE, pageIncludeId)).toException();
    }

    return model;
  }

  protected PageModel readPageModel(String pageId) {
    Path filePath = sourcePath.resolve(YAML.appendTo(pageId));

    if (!Files.exists(filePath)) {
      return new PageModel();
    }

    PageModel model;
    try {
      model = conf.objectMapper.readValue(filePath.toFile(), PageModel.class);
      model.filePath = filePath;
    } catch (IOException e) {
      throw errorFactory.modelNotRead(e, new SourceImpl(SourceType.PAGE, pageId)).toException();
    }

    return model;
  }

  protected PageSetModel readPageSetModel(String pageSetId) {
    Path filePath = sourcePath.resolve(YAML.appendTo(pageSetId));

    if (!Files.exists(filePath)) {
      return new PageSetModel();
    }

    PageSetModel model;
    try {
      model = conf.objectMapper.readValue(filePath.toFile(), PageSetModel.class);
      model.filePath = filePath;
    } catch (IOException e) {
      throw errorFactory.modelNotRead(e, new SourceImpl(SourceType.PAGE_SET, pageSetId)).toException();
    }

    return model;
  }

  protected SiteModel readSiteModel(Path filePath) {
    try {
      return conf.objectMapper.readValue(filePath.toFile(), SiteModel.class);
    } catch (IOException e) {
      throw errorFactory.modelNotRead(e, new SourceImpl(SourceType.SITE, null)).toException();
    }
  }

  protected void reset() {
    model = null;

    pages.values().forEach(PageImpl::destroy);
    pageIncludes.values().forEach(PageIncludeImpl::destroy);
    pageSets.values().forEach(PageSetImpl::destroy);

    pages.clear();
    pageIncludes.clear();
    pageSets.clear();
  }

  @Override
  public Stream<SiteOutput> serve() {
    return createOutputServer().serve();
  }

  @Override
  public SiteOutput serve(String requestUri) {
    Objects.requireNonNull(requestUri, "request URI is null");

    return createOutputServer().serve(requestUri);
  }
}
