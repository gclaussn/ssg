package com.github.gclaussn.ssg.impl.markdown;

import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.github.gclaussn.ssg.Site;
import com.vladsch.flexmark.html.LinkResolver;
import com.vladsch.flexmark.html.LinkResolverFactory;
import com.vladsch.flexmark.html.renderer.LinkResolverBasicContext;

class PageLinkResolverFactory implements LinkResolverFactory {

  private final Site site;

  PageLinkResolverFactory(Site site) {
    this.site = site;
  }

  @Override
  public Set<Class<?>> getAfterDependents() {
    return null;
  }

  @Override
  public Set<Class<?>> getBeforeDependents() {
    return null;
  }

  @Override
  public boolean affectsGlobalScope() {
    return false;
  }

  @Override
  public @NotNull LinkResolver apply(@NotNull LinkResolverBasicContext context) {
    return new PageLinkResolver(site);
  }
}
