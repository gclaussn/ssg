package com.github.gclaussn.ssg.impl.markdown;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.github.gclaussn.ssg.Site;
import com.vladsch.flexmark.ext.admonition.AdmonitionExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;

import de.neuland.jade4j.filter.Filter;
import de.neuland.jade4j.parser.node.Attr;

/**
 * Custom markdown Jade filter, which renders markdown code and is able to resolve page links e.g.:
 * {@code [My link](<pageId>) -> <a href="<pageUrl>">My link</a>}
 */
public class MarkdownFilter implements Filter {

  private final Parser parser;
  private final HtmlRenderer renderer;

  public MarkdownFilter(Site site) {
    AdmonitionExtension admonitionExtension = AdmonitionExtension.create();

    parser = Parser.builder()
        .extensions(Arrays.asList(admonitionExtension))
        .build();

    renderer = HtmlRenderer.builder()
        .extensions(Arrays.asList(admonitionExtension, new PageLinkExtension(site)))
        .build();
  }

  @Override
  public String convert(String source, List<Attr> attributes, Map<String, Object> model) {
    return renderer.render(parser.parse(source));
  }
}
