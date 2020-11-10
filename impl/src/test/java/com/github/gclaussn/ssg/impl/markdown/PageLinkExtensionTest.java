package com.github.gclaussn.ssg.impl.markdown;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.Site;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;

public class PageLinkExtensionTest {

  private Site site;

  private Parser parser;
  private HtmlRenderer renderer;

  @Before
  public void setUp() {
    site = Mockito.mock(Site.class);

    parser = Parser.builder().build();

    renderer = HtmlRenderer.builder()
      .extensions(Arrays.asList(new PageLinkExtension(site)))
      .build();
  }

  @Test
  public void shouldRenderPageLink() {
    String pageId = "posts/2020-06-16-my-post";

    Page page = Mockito.mock(Page.class);
    when(page.getUrl()).thenReturn("news/my-post");

    when(site.hasPage(pageId)).thenReturn(Boolean.TRUE);
    when(site.getPage(pageId)).thenReturn(page);

    String p = renderer.render(parser.parse(String.format("[My post](%s)", pageId)));
    assertThat(p, equalTo("<p><a href=\"news/my-post\">My post</a></p>\n"));
  }

  @Test
  public void shouldNotRenderPageLink() {
    String url = "http://example.org/my-post";

    when(site.hasPage(url)).thenReturn(Boolean.FALSE);

    String p = renderer.render(parser.parse(String.format("[My post](%s)", url)));
    assertThat(p, equalTo("<p><a href=\"http://example.org/my-post\">My post</a></p>\n"));
  }
}
