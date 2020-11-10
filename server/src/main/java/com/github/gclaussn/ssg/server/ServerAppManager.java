package com.github.gclaussn.ssg.server;

import static com.github.gclaussn.ssg.file.SiteFileType.HTML;

import java.io.IOException;

import io.undertow.UndertowMessages;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceChangeListener;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.server.handlers.resource.URLResource;

/**
 * App resource handler to serve the resources of the built server app from the classpath.
 */
public class ServerAppManager implements ResourceManager {

  private final ClassLoader classLoader;

  public ServerAppManager() {
    classLoader = this.getClass().getClassLoader();
  }

  @Override
  public void close() throws IOException {
    // nothing to do here
  }

  @Override
  public Resource getResource(String path) throws IOException {
    String resourceName;
    if (path.length() == 1 || path.indexOf('.') == -1 || HTML.isPresent(path)) {
      // server side routing:
      // map "/" and every route e.g. "/pages" to index.html
      // react-router then performs the client side routing
      resourceName = "app/index.html";
    } else {
      resourceName = String.format("app%s", path);
    }

    return new URLResource(classLoader.getResource(resourceName), path);
  }

  @Override
  public boolean isResourceChangeListenerSupported() {
    return false;
  }

  @Override
  public void registerResourceChangeListener(ResourceChangeListener listener) {
    throw UndertowMessages.MESSAGES.resourceChangeListenerNotSupported();
  }

  @Override
  public void removeResourceChangeListener(ResourceChangeListener listener) {
    throw UndertowMessages.MESSAGES.resourceChangeListenerNotSupported();
  }
}
