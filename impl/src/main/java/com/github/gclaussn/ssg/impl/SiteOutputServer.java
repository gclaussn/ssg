package com.github.gclaussn.ssg.impl;

import static com.github.gclaussn.ssg.file.SiteFileType.HTML;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteOutput;

class SiteOutputServer {

  /** Prefix for Node module file resources. */
  protected static final String NODE_MODULES = String.format("%s/", Site.NODE_MODULES);

  private final SiteImpl site;
  private final SiteModel siteModel;

  SiteOutputServer(SiteImpl site) {
    this(site, site.model);
  }

  SiteOutputServer(SiteImpl site, SiteModel siteModel) {
    this.site = site;
    this.siteModel = siteModel;
  }

  protected SiteOutput map(Path basePath, Path filePath) {
    String relativePath = basePath.relativize(filePath).toString();

    String name;

    // ensure unix file separator
    int index = relativePath.indexOf('\\');
    if (index >= 0) {
      name = relativePath.replace('\\', '/');
    } else {
      name = relativePath;
    }
    
    String path = new StringBuilder(name.length() + 1)
        .append('/')
        .append(HTML.isPresent(name) ? HTML.strip(name) : name)
        .toString();

    if (path.equals("/index")) {
      path = "/";
    }

    SiteOutputImpl siteOutput = new SiteOutputImpl();
    siteOutput.filePath = filePath;
    siteOutput.name = name;
    siteOutput.path = path;

    return siteOutput;
  }

  protected String normalizePath(String path) {
    if (path.length() == 1 && path.charAt(0) == '/') {
      return "/index.html";
    }

    int index = path.lastIndexOf('.');
    if (index == -1) {
      return HTML.appendTo(path);
    } else {
      return path;
    }
  }

  protected Stream<SiteOutput> serve() {
    try {
      Stream<SiteOutput> outputFiles = serveFiles(site.getOutputPath());
      Stream<SiteOutput> publicFiles = serveFiles(site.getPublicPath());
      Stream<SiteOutput> nodeModulesFiles = serveNodeModulesFiles();

      return Stream.of(outputFiles, publicFiles, nodeModulesFiles).reduce(Stream::concat).get();
    } catch (IOException e) {
      throw site.errorFactory.siteFileServeFailed(e).toException();
    }
  }

  protected SiteOutput serve(String requestUri) {
    String path = normalizePath(requestUri);
    
    SiteOutputImpl siteOutput = new SiteOutputImpl();
    siteOutput.name = path.substring(1);
    siteOutput.path = HTML.isPresent(path) ? HTML.strip(path) : path;
    
    if (siteOutput.name.startsWith(NODE_MODULES)) {
      siteOutput.filePath = site.getPath().resolve(siteOutput.name);

      return siteOutput;
    }

    Path outputFilePath = site.getOutputPath().resolve(siteOutput.name);
    Path publicFilePath = site.getPublicPath().resolve(siteOutput.name);

    if (Files.exists(outputFilePath)) {
      siteOutput.filePath = outputFilePath;
    } else {
      siteOutput.filePath = publicFilePath;
    }

    return siteOutput;
  }

  protected Stream<SiteOutput> serveFiles(Path basePath) throws IOException {
    if (!Files.isDirectory(basePath)) {
      return Stream.empty();
    }

    return Files.walk(basePath).filter(Files::isRegularFile).map(filePath -> map(basePath, filePath));
  }

  protected Stream<SiteOutput> serveNodeModulesFiles() throws IOException {
    Path path = site.getPath().resolve(Site.NODE_MODULES);
    if (!Files.isDirectory(path)) {
      return Stream.empty();
    }
    
    Stream<Path> stream = Files.walk(path).filter(Files::isRegularFile);
    if (siteModel.nodeModules != null) {
      stream = stream.filter(new NodeModulesMatcher(siteModel.nodeModules));
    }

    return stream.map(filePath -> map(site.getPath(), filePath));
  }
}
