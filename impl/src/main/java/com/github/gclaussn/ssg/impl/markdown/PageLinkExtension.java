package com.github.gclaussn.ssg.impl.markdown;

import com.github.gclaussn.ssg.Site;
import com.vladsch.flexmark.html.HtmlRenderer.Builder;
import com.vladsch.flexmark.html.HtmlRenderer.HtmlRendererExtension;
import com.vladsch.flexmark.util.options.MutableDataHolder;

class PageLinkExtension implements HtmlRendererExtension {

  private final Site site;

  PageLinkExtension(Site site) {
    this.site = site;
  }

  @Override
  public void rendererOptions(MutableDataHolder options) {
    // nothing to do here
  }

  @Override
  public void extend(Builder rendererBuilder, String rendererType) {
    rendererBuilder.linkResolverFactory(new PageLinkResolverFactory(site));
  }
}
