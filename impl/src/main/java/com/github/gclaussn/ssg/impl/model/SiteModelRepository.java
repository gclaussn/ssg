package com.github.gclaussn.ssg.impl.model;

import static com.github.gclaussn.ssg.SourceType.PAGE;
import static com.github.gclaussn.ssg.SourceType.PAGE_INCLUDE;
import static com.github.gclaussn.ssg.SourceType.PAGE_SET;
import static com.github.gclaussn.ssg.SourceType.SITE;
import static com.github.gclaussn.ssg.SourceType.UNKNOWN;
import static com.github.gclaussn.ssg.event.SiteEventType.LOAD_PAGE_INCLUDE;
import static com.github.gclaussn.ssg.event.SiteEventType.LOAD_PAGE_SET;
import static com.github.gclaussn.ssg.event.SiteEventType.LOAD_SITE;
import static com.github.gclaussn.ssg.file.SiteFileType.HTML;
import static com.github.gclaussn.ssg.file.SiteFileType.JADE;
import static com.github.gclaussn.ssg.file.SiteFileType.YAML;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.PageFilterBean;
import com.github.gclaussn.ssg.PageInclude;
import com.github.gclaussn.ssg.PageProcessorBean;
import com.github.gclaussn.ssg.PageSet;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.SourceType;
import com.github.gclaussn.ssg.data.PageData;
import com.github.gclaussn.ssg.data.PageDataBuilder;
import com.github.gclaussn.ssg.error.SiteError;
import com.github.gclaussn.ssg.error.SiteException;
import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.event.SiteEventBuilder;
import com.github.gclaussn.ssg.impl.SourceImpl;
import com.github.gclaussn.ssg.model.NodeModules;

public class SiteModelRepository implements AutoCloseable {

  protected final Site site;

  protected SiteModel model;

  protected final Map<String, PageImpl> pages;
  protected final Map<String, PageIncludeImpl> pageIncludes;
  protected final Map<String, PageSetImpl> pageSets;

  private final ObjectMapper objectMapper;

  public SiteModelRepository(Site site) {
    this.site = site;

    pages = createMap(PageImpl.class);
    pageIncludes = createMap(PageIncludeImpl.class);
    pageSets = createMap(PageSetImpl.class);

    // configure YAML object mapper
    SimpleModule module = new SimpleModule();
    module.addDeserializer(PageData.class, new PageDataDeserializer());
    module.addDeserializer(PageDataSelectorBeanImpl.class, new PageDataSelectorDeserializer(site));
    module.addDeserializer(PageFilterBeanImpl.class, new PageFilterDeserializer(site));
    module.addDeserializer(PageProcessorBeanImpl.class, new PageProcessorDeserializer(site));

    objectMapper = new ObjectMapper(new YAMLFactory());
    objectMapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
    objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
    objectMapper.registerModule(module);
    objectMapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
    objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
  }

  /**
   * Builds the output name for a given page that is part of a page set.<br>
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
   * @param pageSet The related page set.
   * 
   * @param pageId A specific page ID.
   * 
   * @return The output name, including the HTML file extension.
   */
  protected String buildOutputName(PageSetImpl pageSet, String pageId) {
    if (pageSet.basePath == null) {
      return HTML.appendTo(pageId);
    }

    String subId = extractSubId(pageSet.getId(), pageId);

    return new StringBuilder(pageSet.basePath.length() + subId.length() + 6)
        .append(pageSet.basePath)
        .append('/')
        .append(HTML.appendTo(subId))
        .toString();
  }

  protected String buildUrl(String outputName) {
    String resourceName;
    if (outputName.equals("index.html")) {
      resourceName = StringUtils.EMPTY;
    } else if (HTML.isPresent(outputName)) {
      resourceName = HTML.strip(outputName);
    } else {
      resourceName = outputName;
    }

    return new StringBuilder(resourceName.length() + 1).append('/').append(resourceName).toString();
  }

  @Override
  public void close() {
    reset();
  }

  protected List<String> collectPageIds(String pageSetId) {
    Path path = site.getSourcePath().resolve(pageSetId);

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
      throw SiteError.builder(site)
          .source(new SourceImpl(SourceType.PAGE_SET, pageSetId))
          .errorPageSetNotTraversed(e)
          .toException();
    }

    return pageIds;
  }

  private <V> Map<String, V> createMap(Class<V> valueType) {
    return new ConcurrentHashMap<>(32);
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
    String relativePath = site.getSourcePath().relativize(path).toString();

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

  protected Optional<String> extractSetId(String pageId) {
    // use page ID as intial value
    String pageSetId = pageId;

    int index;
    while ((index = pageSetId.lastIndexOf('/')) != -1) {
      pageSetId = pageSetId.substring(0, index);

      if (model.pageSets.contains(pageSetId)) {
        return Optional.of(pageSetId);
      }
    }

    return Optional.empty();
  }

  protected String extractSubId(String pageSetId, String pageId) {
    return pageId.substring(pageSetId.length() + 1, pageId.length());
  }

  protected Optional<SiteError> filterPage(PageImpl page) {
    PageSetImpl pageSet = pageSets.get(page.setId);

    for (PageFilterBean filter : pageSet.filters) {
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

  public Optional<NodeModules> getNodeModules() {
    if (!isLoaded()) {
      // special case for serving site output without loading all sources
      model = loadSiteModel();
    }

    return Optional.of(model.nodeModules);
  }

  public Page getPage(String pageId) {
    return pages.get(pageId);
  }

  public Set<Page> getPages() {
    return new HashSet<>(pages.values());
  }

  public PageInclude getPageInclude(String pageIncludeId) {
    return pageIncludes.get(pageIncludeId);
  }

  public Set<PageInclude> getPageIncludes() {
    return new HashSet<>(pageIncludes.values());
  }

  public PageSet getPageSet(String pageSetId) {
    return pageSets.get(pageSetId);
  }

  public Set<PageSet> getPageSets() {
    return new HashSet<>(pageSets.values());
  }

  public Source getSource(Path path) {
    return getSource(extractId(path));
  }

  public Source getSource(String id) {
    return new SourceImpl(getSourceType(id), id);
  }

  /**
   * Determines the type of the source with the given ID. The source type is determined by finding the
   * ID within all known pages, page sets and page inclundes.
   * 
   * @param id A specific source ID.
   * 
   * @return The source type or {@link SourceType#UNKNOWN}, if the given source is not known.
   */
  protected SourceType getSourceType(String id) {
    if (id == null) {
      return SITE;
    } else if (pages.containsKey(id)) {
      return PAGE;
    } else if (pageIncludes.containsKey(id)) {
      return PAGE_INCLUDE;
    } else if (pageSets.containsKey(id)) {
      return PAGE_SET;
    }

    if (model.pages.contains(id)) {
      return PAGE;
    } else if (model.pageSets.contains(id)) {
      return PAGE_SET;
    } else if (isPageInclude(id)) {
      return PAGE_INCLUDE;
    }
    
    Optional<String> pageSetId = extractSetId(id);
    if (pageSetId.isPresent()) {
      return PAGE_SET;
    }

    return UNKNOWN;
  }

  public boolean isLoaded() {
    return model != null;
  }

  /**
   * Checks if the given ID is the ID of a page include that is used within a page.
   * 
   * @param id A specific ID.
   * 
   * @return {@code true}, if the ID identifies a page include. Otherwise {@code false}.
   */
  protected boolean isPageInclude(String id) {
    return pages.values().stream().anyMatch(page -> page.includes.contains(id));
  }

  public List<SiteError> load() {
    SiteEventBuilder eventBuilder;

    reset();

    eventBuilder = SiteEvent.builder().type(LOAD_SITE);

    // load site model
    try {
      model = loadSiteModel();

      eventBuilder.buildAndPublish(this::publish);
    } catch (SiteException e) {
      model = null;

      eventBuilder.error(e.getError()).buildAndPublish(this::publish);
      return Collections.singletonList(e.getError());
    }

    List<SiteError> errors = new LinkedList<>();

    // load pages
    model.pages.stream().map(this::loadPage).filter(Optional::isPresent).map(Optional::get).forEach(errors::add);

    // load page sets
    model.pageSets.stream().map(this::loadPageSet).forEach(errors::addAll);

    Queue<String> includeQueue = new LinkedList<>();
    for (PageImpl page : pages.values()) {
      includeQueue.addAll(page.includes);
    }

    // load page includes
    errors.addAll(loadPageIncludes(includeQueue));

    return errors;
  }

  public Optional<SiteError> loadPage(String pageId) {
    pages.remove(pageId);

    SiteEventBuilder eventBuilder = SiteEvent.builder().type(LOAD_PAGE_SET).source(PAGE, pageId);

    // load model
    PageModel model;

    try {
      model = readPageModel(pageId);
    } catch (SiteException e) {
      eventBuilder.error(e.getError()).buildAndPublish(this::publish);
      return Optional.of(e.getError());
    }

    String outputName = orElse(model.outputName, HTML.appendTo(pageId));

    PageImpl page = new PageImpl(site);
    page.data = orElse(model.data, PageData::empty);
    page.dataSelectors = orElse(model.dataSelectors, Collections::emptyList);
    page.id = pageId;
    page.includes = orElse(model.includes, Collections::emptySet);
    page.modelPath = model.filePath;
    page.outputName = outputName;
    page.skip = orElse(model.skip, Boolean.FALSE);
    page.templateName = JADE.appendTo(pageId);
    page.url = buildUrl(outputName);

    // initialize beans
    try {
      page.init();
    } catch (SiteException e) {
      eventBuilder.error(e.getError()).buildAndPublish(this::publish);
      return Optional.of(e.getError());
    }

    publish(eventBuilder.build());
    pages.put(pageId, page);

    return Optional.empty();
  }

  public Optional<SiteError> loadPage(String pageId, String pageSetId) {
    pages.remove(pageId);

    SiteEventBuilder eventBuilder = SiteEvent.builder().type(LOAD_PAGE_SET).source(PAGE, pageId).reference(pageSetId);

    PageSetImpl pageSet = pageSets.get(pageSetId);
    if (pageSet == null) {
      throw new SiteException("page set '%s' could not be found", pageSetId);
    }

    // load model
    PageModel model;

    try {
      model = readPageModel(pageId);
    } catch (SiteException e) {
      eventBuilder.error(e.getError()).buildAndPublish(this::publish);
      return Optional.of(e.getError());
    }

    String outputName = orElse(model.outputName, buildOutputName(pageSet, pageId));

    PageImpl page = new PageImpl(site);
    page.data = orElse(model.data, PageData::empty);
    page.dataSelectors = orElse(model.dataSelectors, Collections::emptyList);
    page.id = pageId;
    page.includes = orElse(model.includes, pageSet.includes);
    page.modelPath = model.filePath;
    page.outputName = outputName;
    page.setId = pageSetId;
    page.skip = orElse(model.skip, pageSet.skip);
    page.subId = extractSubId(pageSetId, pageId);
    page.templateName = JADE.appendTo(pageId);
    page.url = buildUrl(outputName);
    
    if (!Files.exists(site.getSourcePath().resolve(page.templateName))) {
      // if template does not exist, use template of page set
      page.templateName = pageSet.getTemplateName();
    }

    // initialize beans
    try {
      page.init();
    } catch (SiteException e) {
      eventBuilder.error(e.getError()).buildAndPublish(this::publish);
      return Optional.of(e.getError());
    }

    PageDataBuilder dataBuilder = PageData.builder().putRoot(page.data);

    Optional<SiteError> error;

    // process page
    error = processPage(page, dataBuilder);
    if (error.isPresent()) {
      return error;
    }

    page.data = dataBuilder.build();

    // filter page
    error = filterPage(page);
    if (error.isPresent()) {
      return error;
    }

    publish(eventBuilder.build());
    pages.put(pageId, page);

    return Optional.empty();
  }

  protected Optional<SiteError> loadPageInclude(String pageIncludeId) {
    SiteEventBuilder eventBuilder = SiteEvent.builder().type(LOAD_PAGE_INCLUDE).source(PAGE_INCLUDE, pageIncludeId);

    // load model
    PageIncludeModel model;

    try {
      model = readPageIncludeModel(pageIncludeId);
    } catch (SiteException e) {
      eventBuilder.error(e.getError()).buildAndPublish(this::publish);
      return Optional.of(e.getError());
    }

    PageIncludeImpl pageInclude = new PageIncludeImpl(this);
    pageInclude.data = orElse(model.data, PageData::empty);
    pageInclude.id = pageIncludeId;
    pageInclude.includes = orElse(model.includes, Collections::emptySet);
    pageInclude.modelPath = model.filePath;

    String templateName = JADE.appendTo(pageIncludeId);
    if (Files.exists(site.getSourcePath().resolve(templateName))) {
      pageInclude.templateName = templateName;
    }

    publish(eventBuilder.build());
    pageIncludes.put(pageIncludeId, pageInclude);

    return Optional.empty();
  }

  public List<SiteError> loadPageIncludes(Queue<String> queue) {
    queue.forEach(pageIncludes::remove);

    List<SiteError> errors = new LinkedList<>();

    while (!queue.isEmpty()) {
      String pageIncludeId = queue.poll();

      if (pageIncludes.containsKey(pageIncludeId)) {
        continue;
      }

      Optional<SiteError> error = loadPageInclude(pageIncludeId);
      if (error.isPresent()) {
        errors.add(error.get());
        continue;
      }

      PageIncludeImpl pageInclude = pageIncludes.get(pageIncludeId);
      queue.addAll(pageInclude.includes);
    }

    return errors;
  }

  public List<SiteError> loadPageSet(String pageSetId) {
    // remove page set and related pages
    removePageSet(pageSetId);

    SiteEventBuilder eventBuilder = SiteEvent.builder().type(LOAD_PAGE_SET).source(PAGE_SET, pageSetId);

    // load model
    PageSetModel model;

    try {
      model = readPageSetModel(pageSetId);
    } catch (SiteException e) {
      eventBuilder.error(e.getError()).buildAndPublish(this::publish);
      return Collections.singletonList(e.getError());
    }

    PageSetImpl pageSet = new PageSetImpl(site);
    pageSet.basePath = model.basePath;
    pageSet.data = orElse(model.data, PageData::empty);
    pageSet.dataSelectors = orElse(model.dataSelectors, Collections::emptyList);
    pageSet.filters = orElse(model.filters, Collections::emptyList);
    pageSet.id = pageSetId;
    pageSet.includes = orElse(model.includes, Collections::emptySet);
    pageSet.pages = new LinkedHashSet<>();
    pageSet.processors = orElse(model.processors, Collections::emptyList);
    pageSet.templateName = JADE.appendTo(pageSetId);
    pageSet.skip = orElse(model.skip, Boolean.FALSE);

    // initialize beans
    try {
      pageSet.init();
    } catch (SiteException e) {
      eventBuilder.error(e.getError()).buildAndPublish(this::publish);
      return Collections.singletonList(e.getError());
    }

    // collect pages
    List<String> pageIds;
    try {
      pageIds = collectPageIds(pageSetId);
    } catch (SiteException e) {
      eventBuilder.error(e.getError()).buildAndPublish(this::publish);
      return Collections.singletonList(e.getError());
    }

    publish(eventBuilder.build());
    pageSets.put(pageSetId, pageSet);

    // load pages
    List<SiteError> errors = new LinkedList<>();
    for (String pageId : pageIds) {

      Optional<SiteError> error = loadPage(pageId, pageSetId);
      if (error.isPresent()) {
        errors.add(error.get());
      } else {
        pageSet.pages.add(pageId);
      }
    }

    return errors;
  }

  protected SiteModel loadSiteModel() {
    SiteModel model = readSiteModel(site.getPath().resolve(Site.MODEL_NAME));
    model.nodeModules = orElse(model.nodeModules, NodeModulesImpl::new);
    model.pages = orElse(model.pages, Collections::emptySet);
    model.pageSets = orElse(model.pageSets, Collections::emptySet);

    return model;
  }

  private <T> T orElse(T value, Supplier<T> defaultValueSupplier) {
    return value != null ? value : defaultValueSupplier.get();
  }

  private <T> T orElse(T value, T defaultValue) {
    return value != null ? value : defaultValue;
  }

  protected Optional<SiteError> processPage(PageImpl page, PageDataBuilder dataBuilder) {
    PageSetImpl pageSet = pageSets.get(page.setId);

    for (PageProcessorBean processor : pageSet.processors) {
      try {
        dataBuilder.put(processor.getId(), processor.process(page));
      } catch (SiteException e) {
        return Optional.of(e.getError());
      }
    }

    return Optional.empty();
  }

  protected void publish(SiteEvent event) {
    site.getConf().publish(event);
  }

  protected PageModel readPageModel(String pageId) {
    Path filePath = site.getSourcePath().resolve(YAML.appendTo(pageId));

    if (!Files.exists(filePath)) {
      return new PageModel();
    }

    PageModel model;
    try {
      model = objectMapper.readValue(filePath.toFile(), PageModel.class);
      model.filePath = filePath;
    } catch (IOException e) {
      throw SiteError.builder(site).source(PAGE, pageId).errorModelNotRead(e).toException();
    }

    return model;
  }

  protected PageIncludeModel readPageIncludeModel(String pageIncludeId) {
    Path filePath = site.getSourcePath().resolve(YAML.appendTo(pageIncludeId));

    if (!Files.exists(filePath)) {
      return new PageIncludeModel();
    }

    PageIncludeModel model;
    try {
      model = objectMapper.readValue(filePath.toFile(), PageIncludeModel.class);
      model.filePath = filePath;
    } catch (IOException e) {
      throw SiteError.builder(site).source(PAGE_INCLUDE, pageIncludeId).errorModelNotRead(e).toException();
    }

    return model;
  }

  protected PageSetModel readPageSetModel(String pageSetId) {
    Path filePath = site.getSourcePath().resolve(YAML.appendTo(pageSetId));

    if (!Files.exists(filePath)) {
      return new PageSetModel();
    }

    PageSetModel model;
    try {
      model = objectMapper.readValue(filePath.toFile(), PageSetModel.class);
      model.filePath = filePath;
    } catch (IOException e) {
      throw SiteError.builder(site).source(PAGE_SET, pageSetId).errorModelNotRead(e).toException();
    }

    return model;
  }

  protected void removePageSet(String pageSetId) {
    if (!pageSets.containsKey(pageSetId)) {
      return;
    }

    PageSetImpl pageSet = pageSets.remove(pageSetId);

    pageSet.pages.forEach(this.pages::remove);
  }

  protected SiteModel readSiteModel(Path filePath) {
    try {
      return objectMapper.readValue(filePath.toFile(), SiteModel.class);
    } catch (IOException e) {
      throw SiteError.builder(site).source(SITE, null).errorModelNotRead(e).toException();
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
}