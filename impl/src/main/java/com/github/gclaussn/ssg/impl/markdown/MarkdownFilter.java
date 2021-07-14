package com.github.gclaussn.ssg.impl.markdown;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteGeneratorFn;
import com.vladsch.flexmark.ext.admonition.AdmonitionExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;

import de.neuland.jade4j.filter.Filter;
import de.neuland.jade4j.parser.node.Attr;

/**
 * Custom Jade filter, which renders Markdown code. This filter can be used in a Jade template via
 * {@link SiteGeneratorFn#renderMarkdown(String)}:
 * 
 * <pre>
 * :markdown
 *   # Heading
 *   
 *   **This is bold text**
 *
 *   __This is bold text__
 *
 *   *This is italic text*
 * </pre>
 * 
 * or
 * 
 * <pre>
 * !{_.renderMarkdown(_md)}
 * </pre>
 * 
 * Moreover the filter is able to resolve page links e.g.:
 * 
 * <pre>
 * [My link](%pageId%)
 * </pre>
 * 
 * will result in:
 * 
 * <pre>
 * <a href="%pageUrl%">My link</a>
 * </pre>
 */
public class MarkdownFilter implements Filter {

  private final Parser parser;
  private final HtmlRenderer renderer;

  public MarkdownFilter(Site site) {
    AdmonitionExtension admonitionExtension = AdmonitionExtension.create();
    TablesExtension tablesExtension = TablesExtension.create();

    parser = Parser.builder()
        .extensions(Arrays.asList(admonitionExtension, tablesExtension))
        .build();

    renderer = HtmlRenderer.builder()
        .extensions(Arrays.asList(admonitionExtension, tablesExtension, new PageLinkExtension(site)))
        .build();
  }

  @Override
  public String convert(String source, List<Attr> attributes, Map<String, Object> model) {
    return renderer.render(parser.parse(source));
  }
}
