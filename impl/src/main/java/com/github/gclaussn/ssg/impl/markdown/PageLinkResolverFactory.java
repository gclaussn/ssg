package com.github.gclaussn.ssg.impl.markdown;

import java.util.Set;

import com.github.gclaussn.ssg.Site;
import com.vladsch.flexmark.html.LinkResolver;
import com.vladsch.flexmark.html.LinkResolverFactory;
import com.vladsch.flexmark.html.renderer.LinkResolverContext;

class PageLinkResolverFactory implements LinkResolverFactory {

  private final Site site;

  PageLinkResolverFactory(Site site) {
    this.site = site;
  }

  @Override
  public Set<Class<? extends LinkResolverFactory>> getAfterDependents() {
    return null;
  }

  @Override
  public Set<Class<? extends LinkResolverFactory>> getBeforeDependents() {
    return null;
  }

  @Override
  public boolean affectsGlobalScope() {
    return false;
  }

  @Override
  public LinkResolver create(LinkResolverContext context) {
    return new PageLinkResolver(site);
  }
}
