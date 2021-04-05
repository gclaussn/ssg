package com.github.gclaussn.ssg.npm;

/**
 * Information about a Node.js package of a specific version, which was retrieved from a NPM
 * registry.
 */
public interface NodePackageInfo extends NodePackage {

  /**
   * Returns the SHA checksum of the package's distribution.
   * 
   * @return The checksum.
   */
  String getChecksum();

  /**
   * Gets the name of the distribution's tarball file.
   * 
   * @return The distribution's file name.
   */
  String getFileName();

  /**
   * Gets the download URL of the tarball.
   * 
   * @return The URL.
   */
  String getUrl();
}
