package com.github.gclaussn.ssg.server.file;

import java.util.Objects;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.conf.SiteProperty;

class SiteFileWatcherFactory {

  protected static SiteFileWatcherFactory of(Site site) {
    Objects.requireNonNull(site, "site is null");

    return site.getConfiguration().inject(new SiteFileWatcherFactory(site));
  }

  @SiteProperty(name = SiteFileWatcher.TYPE, defaultValue = "WATCHER_SERVICE")
  protected SiteFileWatcherType type;

  private final Site site;

  SiteFileWatcherFactory(Site site) {
    this.site = site;
  }

  protected SiteFileWatcher create() {
    return create(this.type);
  }

  protected SiteFileWatcher create(SiteFileWatcherType type) {
    AbstractFileWatcher fileWatcher;
    switch (type) {
      case POLLING:
        fileWatcher = new PollingFileWatcher();
        break;
      case WATCHER_SERVICE:
        fileWatcher = new SiteFileWatcherImpl();
        break;
      default:
        throw new IllegalArgumentException(String.format("Unsupported file watcher type '%s'", type));
    }

    fileWatcher.site = site;

    return fileWatcher;
  }
}
