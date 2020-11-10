package com.github.gclaussn.ssg.server;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.file.SiteFileWatcher;
import com.github.gclaussn.ssg.server.domain.SiteResource;
import com.github.gclaussn.ssg.server.domain.event.SiteEventResource;
import com.github.gclaussn.ssg.server.domain.page.PageResource;
import com.github.gclaussn.ssg.server.domain.page.PageSetResource;
import com.github.gclaussn.ssg.server.domain.source.SourceResource;
import com.github.gclaussn.ssg.server.provider.CustomExceptionMapper;
import com.github.gclaussn.ssg.server.provider.CustomJsonProvider;

@ApplicationPath("/")
public class ServerApi extends Application {

  private final Site site;

  private final SiteFileWatcher siteFileWatcher;

  public ServerApi(Site site, SiteFileWatcher siteFileWatcher) {
    this.site = site;
    this.siteFileWatcher = siteFileWatcher;
  }

  @Override
  public Set<Class<?>> getClasses() {
    Set<Class<?>> classes = new HashSet<>();
    classes.add(CustomExceptionMapper.class);
    classes.add(CustomJsonProvider.class);

    return classes;
  }

  @Override
  public Set<Object> getSingletons() {
    List<AbstractResource> resources = new LinkedList<>();

    resources.add(new ServerResource(siteFileWatcher));

    // Domain
    resources.add(new PageResource());
    resources.add(new PageSetResource());
    resources.add(new SiteEventResource());
    resources.add(new SiteResource());
    resources.add(new SourceResource());

    resources.forEach(this::init);

    return new HashSet<>(resources);
  }

  protected void init(AbstractResource resource) {
    resource.init(site);
  }
}
