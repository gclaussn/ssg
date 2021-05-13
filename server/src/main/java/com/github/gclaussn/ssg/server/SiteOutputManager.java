package com.github.gclaussn.ssg.server;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteOutput;

import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.server.handlers.resource.Resource;

public class SiteOutputManager extends FileResourceManager {

  private final Site site;

  public SiteOutputManager(Site site) {
    super(site.getPath().toFile());
    this.site = site;
  }

  @Override
  public Resource getResource(String path) {
    if (path.isEmpty() || path.length() == 1) {
      // if index is requested: http://<host>:<port> or http://<host>:<port>/
      return super.getResource(path);
    }

    SiteOutput siteOutput = site.serve(path);

    return super.getResource(site.getPath().relativize(siteOutput.getFilePath()).toString());
  }
}
