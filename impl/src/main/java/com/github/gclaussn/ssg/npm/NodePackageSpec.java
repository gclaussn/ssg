package com.github.gclaussn.ssg.npm;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

/**
 * Defines, which Node packages to install and which files of those packages to include in the
 * site.<br>
 * The specification is defined within the {@code site.yaml} file via property "node".
 */
public interface NodePackageSpec {

  /**
   * Provides a list of glob pattern, that specify which files of the {@code node_modules} directory
   * should be included in the site's output.
   * 
   * @return A list, containing file include glob patterns.
   */
  List<String> getIncludes();

  Predicate<Path> getMatcher();

  List<NodePackage> getPackages();
}
