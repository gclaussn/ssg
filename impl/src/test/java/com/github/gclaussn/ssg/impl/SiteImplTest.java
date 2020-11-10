package com.github.gclaussn.ssg.impl;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.SiteErrorType;
import com.github.gclaussn.ssg.SiteException;
import com.github.gclaussn.ssg.SourceType;
import com.github.gclaussn.ssg.data.PageData;
import com.github.gclaussn.ssg.file.SiteFileType;

public class SiteImplTest {

  private SiteImpl site;

  @Before
  public void setUp() {
    Path sitePath = Paths.get("./src/test/resources")
        .resolve(SiteImplTest.class.getPackage().getName().replace('.', '/'));

    site = new SiteImpl(sitePath, new SiteConfImpl());
  }

  @Test
  public void testBuildMetadata() {
    PageImpl page = new PageImpl(null, new PageModel());
    page.id = "index-page";
    page.url = "/index";

    page.model.outputName = "index.html";

    Map<String, Object> metadata = site.buildMetadata(page);
    assertThat(metadata, notNullValue());
    assertThat(metadata.size(), is(3));
    assertThat(metadata.get("id"), is(page.id));
    assertThat(metadata.get("setId"), nullValue());
    assertThat(metadata.get("url"), equalTo("/index"));
  }

  @Test
  public void testBuildOutputName() {
    PageSetImpl pageSet = new PageSetImpl(null, new PageSetModel());
    pageSet.id = "posts";

    // base path not set
    assertThat(site.buildOutputName("posts/my-post", pageSet), equalTo("posts/my-post.html"));

    // base path set
    pageSet.model.basePath = "news";

    assertThat(site.buildOutputName("posts/my-post", pageSet), equalTo("news/my-post.html"));
    assertThat(site.buildOutputName("posts/2020/05/my-post", pageSet), equalTo("news/2020/05/my-post.html"));
  }

  @Test
  public void testBuildUrl() {
    PageModel model = new PageModel();

    model.outputName = "index.html";
    assertThat(site.buildUrl(model), equalTo("/"));

    model.outputName = "index";
    assertThat(site.buildUrl(model), equalTo("/index"));
  }

  @Test
  public void testExtractId() {
    Path yamlPath = site.getSourcePath().resolve("test/posts.yaml");
    Path jadePath = site.getSourcePath().resolve("test/posts.jade");

    assertThat(site.extractId(yamlPath), equalTo("test/posts"));
    assertThat(site.extractId(jadePath), equalTo("test/posts"));
  }

  @Test
  public void testGetGenerator() {
    assertThat(site.getGenerator(), notNullValue());
  }

  @Test
  public void testGetPath() {
    assertThat(site.getPath(), equalTo(site.getPath()));

    assertThat(site.getPublicPath(), equalTo(site.getPath().resolve(Site.PUBLIC)));
    assertThat(site.getSourcePath(), equalTo(site.getPath().resolve(Site.SOURCE)));
    assertThat(site.getOutputPath(), equalTo(site.getPath().resolve(Site.OUTPUT)));
  }

  @Test
  public void testReadSiteModelEmpty() {
    try {
      site.readSiteModel(site.getPath().resolve("empty-site.yaml"));
      fail("should throw SiteException");
    } catch (SiteException e) {
      assertThat(e.getMessage(), notNullValue());

      SiteError error = e.getError();
      assertThat(error, notNullValue());
      assertThat(error.getCause(), notNullValue());
      assertThat(error.getMessage(), containsString("site"));
      assertThat(error.getSource().isPresent(), is(true));
      assertThat(error.getSource().get().getId(), nullValue());
      assertThat(error.getSource().get().getType(), is(SourceType.SITE));
      assertThat(error.getLocation().isPresent(), is(true));
      assertThat(error.getLocation().get().getPath(), equalTo(site.getPath().resolve(Site.MODEL_NAME)));
      assertThat(error.getLocation().get().getLine(), is(1));
      assertThat(error.getLocation().get().getColumn(), is(1));
      assertThat(error.getType(), is(SiteErrorType.MODEL));
    }
  }

  @Test
  public void testReadSiteModelNotFound() {
    try {
      site.readSiteModel(site.getPath().resolve("not-found.yaml"));
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
    List<String> pageIds = site.collectPageIds("not-empty-set");
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

    Optional<SiteError> error = site.loadPage(pageId);
    assertThat(error.isPresent(), is(false));

    PageImpl page = site.pages.get(pageId);
    assertThat(page, notNullValue());
    assertThat(page.getData(), notNullValue());
    assertThat(page.getData().getRootMap().size(), is(1));
    assertThat(page.getData().get(PageData.META), notNullValue());
    assertThat(page.getId(), equalTo(pageId));
    assertThat(page.getModelPath().isPresent(), is(false));
    assertThat(page.getPageIncludes(), notNullValue());
    assertThat(page.getPageIncludes().isEmpty(), is(true));
    assertThat(page.getOutputName(), equalTo(SiteFileType.HTML.appendTo(pageId)));
    assertThat(page.getTemplateName(), equalTo(SiteFileType.JADE.appendTo(pageId)));

    assertThat(site.pages.containsKey(pageId), is(true));
  }

  /**
   * Page with data and nested data structures.
   */
  @Test
  public void shouldLoadPageWithData() {
    String pageId = "page-with-data";

    Optional<SiteError> error = site.loadPage(pageId);
    assertThat(error.isPresent(), is(false));

    PageImpl page = site.pages.get(pageId);
    assertThat(page, notNullValue());
    assertThat(page.getData(), notNullValue());
    assertThat(page.getData().get("x").as(String.class), equalTo("y"));

    Path filePath = site.getSourcePath().resolve("page-with-data.yaml");
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

    assertThat(site.pages.containsKey(pageId), is(true));
  }

  /**
   * Page with custom output name as well as includes.
   */
  @Test
  public void shouldLoadPageWithCustomTemplate() {
    String pageId = "page-with-custom-template";

    Optional<SiteError> error = site.loadPage(pageId);
    assertThat(error.isPresent(), is(false));

    PageImpl page = site.pages.get(pageId);
    assertThat(page, notNullValue());
    assertThat(page.model.includes, notNullValue());
    assertThat(page.model.includes.size(), is(3));
    assertThat(page.model.includes.contains("common/include-a"), is(true));
    assertThat(page.model.includes.contains("common/include-b"), is(true));
    assertThat(page.model.includes.contains("common/include-c"), is(true));
    assertThat(page.getOutputName(), equalTo("x.html"));
    assertThat(page.getOutputPath(), equalTo(site.getOutputPath().resolve("x.html")));
    assertThat(page.getTemplateName(), equalTo("page-with-custom-template.jade"));

    assertThat(site.pages.containsKey(pageId), is(true));
  }
}
