package com.github.gclaussn.ssg.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.data.PageData;
import com.github.gclaussn.ssg.data.PageDataNode;
import com.github.gclaussn.ssg.data.PageDataNodeType;

public class SiteGeneratorImplTest {

  private SiteGeneratorImpl generator;

  @Before
  public void setUp() {
    String packageName = SiteGeneratorImplTest.class.getPackage().getName();
    Path testResources = Paths.get("./src/test/resources");

    Path sitePath = testResources.resolve(packageName.replace('.', '/'));

    SiteImpl site = new SiteImpl(new SiteBuilderImpl(), sitePath);

    generator = new SiteGeneratorImpl(site);
  }

  @Test
  public void testBuildMetadata() {
    Page page = Mockito.mock(Page.class);
    when(page.getId()).thenReturn("index-page");
    when(page.getSubId()).thenReturn(Optional.empty());
    when(page.getUrl()).thenReturn("/index");

    Map<String, Object> meta = generator.buildMetadata(page);
    assertThat(meta, notNullValue());
    assertThat(meta.size(), is(3));
    assertThat(meta.get("id"), is(page.getId()));
    assertThat(meta.get("setId"), nullValue());
    assertThat(meta.get("url"), equalTo(page.getUrl()));
  }

  @Test
  public void testCompileExtensions() {
    PageData compiled = generator.compileExtensions(Collections.singleton(new SiteGeneratorImplTest()));
    assertThat(compiled.has("com/github/gclaussn/ssg/impl/SiteGeneratorImplTest"), is(true));

    PageDataNode node;

    node = compiled.get("com/github/gclaussn/ssg/impl/SiteGeneratorImplTest");
    assertThat(node, notNullValue());
    assertThat(node.getType(), is(PageDataNodeType.OTHER));
    assertThat(node.isNull(), is(false));
  }

  @Test
  public void testNormalizeId() {
    assertThat(generator.normalizeId(""), equalTo(""));
    assertThat(generator.normalizeId("page-a"), equalTo("pageA"));
    assertThat(generator.normalizeId("page_b"), equalTo("pageB"));
    assertThat(generator.normalizeId("page-set1/sub/x_y_z"), equalTo("pageSet1/sub/xYZ"));
  }
}
