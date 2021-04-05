package com.github.gclaussn.ssg.server;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.server.provider.CustomExceptionMapper;
import com.github.gclaussn.ssg.server.provider.CustomJsonProvider;

@ApplicationPath("/")
public class ServerApi extends Application {

  private final Site site;

  private final List<AbstractResource> resources;

  public ServerApi(Site site) {
    this.site = site;
    
    resources = new LinkedList<>();
  }

  public void add(AbstractResource resource) {
    resources.add(resource);
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
    resources.forEach(this::init);

    return new HashSet<>(resources);
  }

  protected void init(AbstractResource resource) {
    resource.site = site;
  }
}
