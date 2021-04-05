package com.github.gclaussn.ssg.npm;

import java.io.InputStream;

import com.github.gclaussn.ssg.impl.npm.NodePackageManagerImpl;

/**
 * Node.js package manager that communicates with a NPM registry via HTTP.
 */
public interface NodePackageManager extends NodePackageRegistry, AutoCloseable {

  /** Url of the default NPM registry. */
  static final String DEFAULT_REGISTRY_URL = "https://registry.npmjs.org";

  /** Node.js modules folder. */
  static final String NODE_MODULES = "node_modules";

  /**
   * Creates a new package manager, that uses a specific NPM registry.
   * 
   * @param registryUrl The URL of a specific NPM registry.
   * 
   * @return The newly created package manager.
   */
  static NodePackageManager of(String registryUrl) {
    return new NodePackageManagerImpl(registryUrl);
  }

  /**
   * Creates a new package manager, using the default NPM registry.
   * 
   * @return The newly created package manager.
   */
  static NodePackageManager ofDefault() {
    return of(DEFAULT_REGISTRY_URL);
  }

  /**
   * Closes the package manager.
   */
  @Override
  void close();

  /**
   * Downloads the tarball of the given Node.js package from a NPM registry.
   * 
   * @param nodePackage A specific node package, providing package name and version.
   * 
   * @return The node package's input stream.
   */
  InputStream download(NodePackage nodePackage);

  /**
   * Gets the information about the given Node.js package from a NPM registry.
   * 
   * @param nodePackage A specific node package, providing package name and version.
   * 
   * @return The package information.
   */
  NodePackageInfo getPackage(NodePackage nodePackage);
}
