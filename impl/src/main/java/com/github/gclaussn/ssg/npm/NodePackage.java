package com.github.gclaussn.ssg.npm;

/**
 * Node.js package.
 */
public interface NodePackage {

  /**
   * Returns the name of the package - e.g. "jquery"
   * 
   * @return The package name.
   */
  String getName();

  /**
   * Returns the version of the package - e.g. "3.6.0"
   * 
   * @return The package version.
   */
  String getVersion();
}
