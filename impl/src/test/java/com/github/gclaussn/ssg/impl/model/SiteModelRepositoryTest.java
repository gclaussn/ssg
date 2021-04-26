package com.github.gclaussn.ssg.impl.model;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.SiteErrorType;
import com.github.gclaussn.ssg.SiteException;
import com.github.gclaussn.ssg.data.PageData;
import com.github.gclaussn.ssg.data.PageDataBuilder;
import com.github.gclaussn.ssg.data.PageDataNodeType;
import com.github.gclaussn.ssg.file.SiteFileType;

public class SiteModelRepositoryTest {

  private SiteModelRepository repository;

  @Before
  public void setUp() {
    String packageName = SiteModelRepositoryTest.class.getPackage().getName().replace('.', '/');
    Path sitePath = Paths.get("./src/test/resources").resolve(packageName);

    repository = new SiteModelRepository(Site.from(sitePath));
  }

  @Test
  public void testAddMetadata() {
    PageImpl page = new PageImpl(null);
    page.id = "index-page";
    page.markdown = "Test 123";
    page.subId = null;
    page.url = "/index";

    PageDataBuilder dataBuilder = PageData.builder().putRoot(Collections.singletonMap("x", "y"));

    repository.addMetadata(dataBuilder, page);

    PageData data = dataBuilder.build();
    assertThat(data, notNullValue());
    assertThat(data.has(PageData.ID), is(true));
    assertThat(data.get(PageData.ID).getType(), is(PageDataNodeType.STRING));
    assertThat(data.get(PageData.ID).as(String.class), equalTo(page.id));
    assertThat(data.has(PageData.MARKDOWN), is(true));
    assertThat(data.get(PageData.MARKDOWN).getType(), is(PageDataNodeType.STRING));
    assertThat(data.get(PageData.MARKDOWN).as(String.class), equalTo(page.getMarkdown().get()));
    assertThat(data.has(PageData.SET_ID), is(false));
    assertThat(data.has(PageData.SUB_ID), is(false));
    assertThat(data.has(PageData.URL), is(true));
    assertThat(data.has("x"), is(true));
    assertThat(data.get("x").getType(), is(PageDataNodeType.STRING));
    assertThat(data.get("x").as(String.class), equalTo("y"));
  }

  @Test
  public void testBuildOutputName() {
    PageSetImpl pageSet = new PageSetImpl(null);
    pageSet.id = "posts";

    // base path not set
    assertThat(repository.buildOutputName(pageSet, "posts/my-post"), equalTo("posts/my-post.html"));

    // base path set
    pageSet.basePath = "news";

    assertThat(repository.buildOutputName(pageSet, "posts/my-post"), equalTo("news/my-post.html"));
    assertThat(repository.buildOutputName(pageSet, "posts/2020/05/my-post"), equalTo("news/2020/05/my-post.html"));

    // base path empty
    pageSet.basePath = "";

    assertThat(repository.buildOutputName(pageSet, "posts/2020/05/my-post"), equalTo("2020/05/my-post.html"));
  }

  @Test
  public void testBuildUrl() {
    assertThat(repository.buildUrl("index.html"), equalTo("/"));
    assertThat(repository.buildUrl("index"), equalTo("/index"));
  }

  @Test
  public void testExtractId() {
    Path yamlPath = repository.site.getSourcePath().resolve("test/posts.yaml");
    Path jadePath = repository.site.getSourcePath().resolve("test/posts.jade");
    Path mdPath = repository.site.getSourcePath().resolve("test/posts.md");

    assertThat(repository.extractId(yamlPath), equalTo("test/posts"));
    assertThat(repository.extractId(jadePath), equalTo("test/posts"));
    assertThat(repository.extractId(mdPath), equalTo("test/posts"));
  }

  @Test
  public void testFindPageSetId() {
    repository.model = new SiteModel();
    repository.model.pageSets = Collections.singleton("posts");

    Optional<String> setId = repository.findPageSetId("posts/hello-world");
    assertThat(setId.isPresent(), is(true));
    assertThat(setId.get(), equalTo("posts"));
  }

  @Test
  public void testReadSiteModelEmpty() {
    try {
      repository.readSiteModel(repository.site.getPath().resolve("empty-site.yaml"));
      fail("should throw SiteException");
    } catch (SiteException e) {
      assertThat(e.getMessage(), notNullValue());

      SiteError error = e.getError();
      assertThat(error, notNullValue());
      assertThat(error.getCause(), notNullValue());
      assertThat(error.getMessage(), containsString("site"));
      assertThat(error.getSource().isPresent(), is(true));
      assertThat(error.getSource().get().getId(), nullValue());
      assertThat(error.getSource().get().getType(), nullValue());
      assertThat(error.getLocation().isPresent(), is(true));
      assertThat(error.getLocation().get().getPath(), equalTo(repository.site.getPath().resolve(Site.MODEL_NAME)));
      assertThat(error.getLocation().get().getLine(), is(1));
      assertThat(error.getLocation().get().getColumn(), is(1));
      assertThat(error.getType(), is(SiteErrorType.MODEL));
    }
  }

  @Test
  public void testReadSiteModelNotFound() {
    try {
      repository.readSiteModel(repository.site.getPath().resolve("not-found.yaml"));
      fail("should throw SiteException");
    } catch (SiteException e) {
      assertThat(e.getMessage(), notNullValue());

      SiteError error = e.getError();
      assertThat(error, notNullValue());
      assertThat(error.getType(), is(SiteErrorType.IO));
    }
  }

  @Test
  public void shouldCollectPageIds() {
    List<String> pageIds = repository.collectPageIds("not-empty-set");
    assertThat(pageIds, notNullValue());
    assertThat(pageIds.size(), is(3));
    assertThat(pageIds.get(0), equalTo("not-empty-set/1"));
    assertThat(pageIds.get(1), equalTo("not-empty-set/2"));
    assertThat(pageIds.get(2), equalTo("not-empty-set/sub/3"));
  }

  /**
   * Page without YAML file.
   */
  @Test
  public void shouldLoadPageWithoutYaml() {
    String pageId = "index";

    Optional<SiteError> error = repository.loadPage(pageId);
    assertThat(error.isPresent(), is(false));

    PageImpl page = repository.pages.get(pageId);
    assertThat(page, notNullValue());
    assertThat(page.getData(), notNullValue());
    assertThat(page.getData().getRootMap().size(), is(2));
    assertThat(page.getData().has(PageData.ID), is(true));
    assertThat(page.getData().has(PageData.SUB_ID), is(false));
    assertThat(page.getData().has(PageData.URL), is(true));
    assertThat(page.getId(), equalTo(pageId));
    assertThat(page.getModelPath().isPresent(), is(false));
    assertThat(page.getPageIncludes(), notNullValue());
    assertThat(page.getPageIncludes().isEmpty(), is(true));
    assertThat(page.getOutputName(), equalTo(SiteFileType.HTML.appendTo(pageId)));
    assertThat(page.getTemplateName(), equalTo(SiteFileType.JADE.appendTo(pageId)));

    assertThat(repository.pages.containsKey(pageId), is(true));
  }

  /**
   * Page with data and nested data structures.
   */
  @Test
  public void shouldLoadPageWithData() {
    String pageId = "page-with-data";

    Optional<SiteError> error = repository.loadPage(pageId);
    assertThat(error.isPresent(), is(false));

    PageImpl page = repository.pages.get(pageId);
    assertThat(page, notNullValue());
    assertThat(page.getData(), notNullValue());
    assertThat(page.getData().get("x").as(String.class), equalTo("y"));

    Path filePath = repository.site.getSourcePath().resolve("page-with-data.yaml");
    assertThat(page.getModelPath().isPresent(), is(true));
    assertThat(page.getModelPath().get(), equalTo(filePath));

    Map<String, Object> map = page.getData().get("map").asMap();
    assertThat(map, notNullValue());
    assertThat(map.get("a"), equalTo("b"));
    assertThat(map.get("c"), equalTo("d"));

    List<Object> list = page.getData().get("list").asList();
    assertThat(list, notNullValue());
    assertThat(list.size(), is(3));
    assertThat(list.get(0), equalTo("a"));
    assertThat(list.get(1), equalTo("b"));
    assertThat(list.get(2), equalTo("c"));

    assertThat(repository.pages.containsKey(pageId), is(true));
  }

  /**
   * Page with custom output name as well as includes.
   */
  @Test
  public void shouldLoadPageWithCustomTemplate() {
    String pageId = "page-with-custom-template";

    Optional<SiteError> error = repository.loadPage(pageId);
    assertThat(error.isPresent(), is(false));

    PageImpl page = repository.pages.get(pageId);
    assertThat(page, notNullValue());
    assertThat(page.includes, notNullValue());
    assertThat(page.includes.size(), is(3));
    assertThat(page.includes.contains("common/include-a"), is(true));
    assertThat(page.includes.contains("common/include-b"), is(true));
    assertThat(page.includes.contains("common/include-c"), is(true));
    assertThat(page.getOutputName(), equalTo("x.html"));
    assertThat(page.getOutputPath(), equalTo(repository.site.getOutputPath().resolve("x.html")));
    assertThat(page.getTemplateName(), equalTo("page-with-custom-template.jade"));

    assertThat(repository.pages.containsKey(pageId), is(true));
  }

  @Test
  public void shouldLoadPageWithMarkdownFrontMatter() {
    String pageId = "page-with-markdown-front-matter";

    Optional<SiteError> error = repository.loadPage(pageId);
    assertThat(error.isPresent(), is(false));

    PageImpl page = repository.pages.get(pageId);
    assertThat(page, notNullValue());
    assertThat(page.getData().has("x"), is(true));
    assertThat(page.getData().get("x").getType(), is(PageDataNodeType.STRING));
    assertThat(page.getData().get("x").as(String.class), equalTo("y"));
    assertThat(page.getMarkdown().isPresent(), is(true));
    assertThat(page.getMarkdown().get(), containsString("Test 123"));
    assertThat(page.getModelPath().isPresent(), is(true));

    Path modelPath = repository.site.getSourcePath().resolve(pageId + ".md");
    assertThat(page.getModelPath().get(), equalTo(modelPath));
  }
}
