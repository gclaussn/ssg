package com.github.gclaussn.ssg.impl.file;

import java.util.Objects;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.conf.SiteProperty;
import com.github.gclaussn.ssg.file.SiteFileWatcher;
import com.github.gclaussn.ssg.file.SiteFileWatcherType;

public class FileWatcherFactory {

  public static FileWatcherFactory of(Site site) {
    Objects.requireNonNull(site, "site is null");

    return site.getConf().inject(new FileWatcherFactory(site));
  }

  @SiteProperty(name = SiteFileWatcher.TYPE, defaultValue = "WATCHER_SERVICE")
  protected SiteFileWatcherType type;

  private final Site site;

  FileWatcherFactory(Site site) {
    this.site = site;
  }

  public SiteFileWatcher create() {
    return create(this.type);
  }

  public SiteFileWatcher create(SiteFileWatcherType type) {
    AbstractFileWatcher fileWatcher = of(type);
    fileWatcher.site = site;

    return fileWatcher;
  }

  protected AbstractFileWatcher of(SiteFileWatcherType type) {
    switch (type) {
      case POLLING:
        return new PollingFileWatcher();
      case WATCHER_SERVICE:
        return new SiteFileWatcherImpl();
      default:
        throw new IllegalArgumentException(String.format("Unsupported file watcher type '%s'", type));
    }
  }
}
