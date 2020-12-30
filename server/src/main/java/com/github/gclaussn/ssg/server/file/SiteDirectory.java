package com.github.gclaussn.ssg.server.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

class SiteDirectory {

  private final Path path;

  private final Map<Path, SiteFile> siteFiles;
  private final Map<Path, SiteDirectory> siteDirectories;

  SiteDirectory(Path path) {
    this.path = path;

    siteFiles = new HashMap<>();
    siteDirectories = new HashMap<>();
  }

  protected void clear() {
    siteFiles.clear();

    siteDirectories.values().forEach(SiteDirectory::clear);
    siteDirectories.clear();
  }

  protected void poll(Set<SiteFile> changeSet, boolean createdOnly) throws IOException {
    if (createdOnly) {
      pollCreated(changeSet);
    } else {
      pollFiles(changeSet);
      pollDirectories(changeSet);
    }
  }

  private void pollCreated(Set<SiteFile> changeSet) throws IOException {
    Iterator<Path> it;

    it = siteDirectories.keySet().iterator();
    while (it.hasNext()) {
      SiteDirectory directory = siteDirectories.get(it.next());

      if (Files.isDirectory(directory.path)) {
        directory.poll(changeSet, true);
      }
    }

    it = Files.list(path).iterator();
    while (it.hasNext()) {
      Path filePath = it.next();

      if (siteFiles.containsKey(filePath)) {
        continue;
      }
      if (siteDirectories.containsKey(filePath)) {
        continue;
      }

      if (Files.isRegularFile(filePath)) {
        SiteFile siteFile = new SiteFile(filePath);
        siteFile.poll();

        siteFiles.put(filePath, siteFile);

        changeSet.add(siteFile);
      } else {
        SiteDirectory siteDirectory = new SiteDirectory(filePath);
        siteDirectories.put(filePath, siteDirectory);

        siteDirectory.poll(changeSet, true);
      }
    }
  }

  private void pollDirectories(Set<SiteFile> changeSet) throws IOException {
    Iterator<Path> it = siteDirectories.keySet().iterator();
    while (it.hasNext()) {
      SiteDirectory directory = siteDirectories.get(it.next());

      if (Files.isDirectory(directory.path)) {
        directory.poll(changeSet, false);
      } else {
        directory.clear();

        it.remove();
      }
    }
  }

  private void pollFiles(Set<SiteFile> changeSet) throws IOException {
    Iterator<Path> it = siteFiles.keySet().iterator();
    while (it.hasNext()) {
      SiteFile file = siteFiles.get(it.next());

      if (!Files.isRegularFile(file.path)) {
        it.remove();

        file.deleted = true;

        changeSet.add(file);
        continue;
      }

      if (file.poll()) {
        changeSet.add(file);
      }
    }
  }
}
