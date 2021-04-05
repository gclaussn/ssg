package com.github.gclaussn.ssg.impl.markdown;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.Site;
import com.vladsch.flexmark.html.LinkResolver;
import com.vladsch.flexmark.html.renderer.LinkResolverBasicContext;
import com.vladsch.flexmark.html.renderer.LinkType;
import com.vladsch.flexmark.html.renderer.ResolvedLink;
import com.vladsch.flexmark.util.ast.Node;

class PageLinkResolver implements LinkResolver {

  private final Site site;

  PageLinkResolver(Site site) {
    this.site = site;
  }

  @Override
  public ResolvedLink resolveLink(Node node, LinkResolverBasicContext context, ResolvedLink link) {
    String pageId = link.getUrl();
    if (!site.hasPage(pageId)) {
      return link;
    }

    Page page = site.getPage(pageId);
    return new ResolvedLink(LinkType.LINK, page.getUrl());
  }
}
