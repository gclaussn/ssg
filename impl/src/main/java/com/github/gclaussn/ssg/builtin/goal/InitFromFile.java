package com.github.gclaussn.ssg.builtin.goal;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.conf.SiteConsole;
import com.github.gclaussn.ssg.plugin.SitePluginGoal;

class InitFromFile extends SimpleFileVisitor<Path> implements SitePluginGoal {

  protected SiteConsole console;

  private final String template;
  private final List<Path> resources;

  InitFromFile(String template) {
    this.template = template;

    resources = new LinkedList<Path>();
  }

  @Override
  public void execute(Site site) {
    Path templatePath = Paths.get(template);

    console.log("Listing resources under %s", template);
    try {
      Files.walkFileTree(templatePath, this);
    } catch (IOException e) {
      throw new RuntimeException("Template resources could not be listed", e);
    }

    for (Path resource : resources) {
      String relativePath = templatePath.relativize(resource).toString();

      Path target = site.getPath().resolve(relativePath);

      console.log("Copying resource: %s", relativePath);
      try {
        Files.createDirectories(target.getParent());
        Files.copy(resource, target);
      } catch (IOException e) {
        throw new RuntimeException(String.format("Site resource '%s' could not be copied", resource), e);
      }
    }

    resources.clear();
  }

  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
    resources.add(file);

    return FileVisitResult.CONTINUE;
  }
}
