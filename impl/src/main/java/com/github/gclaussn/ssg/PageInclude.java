package com.github.gclaussn.ssg;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

import com.github.gclaussn.ssg.data.PageData;

/**
 * A reusable source, providing data and/or template code. A page include can be used via JADE
 * {@code include} or {@code extends} syntax.
 */
public interface PageInclude extends Source {

  /**
   * Checks if the page include depends on the given source.
   * 
   * @param source Another page include.
   * 
   * @return {@code true}, if the page include does depend on the source. Otherwise {@code false}.
   */
  boolean dependsOn(Source source);

  /**
   * Returns the data, specified within the page include's YAML file.
   * 
   * @return The page include data.
   */
  PageData getData();

  /**
   * Returns the path to the page include's model (YAML) file.
   * 
   * @return The model path, if the file exists. Otherwise an empty optional.
   */
  Optional<Path> getModelPath();

  /**
   * Provides all includes, that are used by the page include.
   * 
   * @return A set, containing all used {@link PageInclude}s.
   */
  Set<PageInclude> getPageIncludes();

  /**
   * Provides the name of the JADE template, that is used when the {@link PageInclude} is included
   * (via JADE {@code include}) or extended (via JADE {@code extends}).
   * 
   * @return The JADE template name or an empty optional, if the page include provides only data.
   */
  Optional<String> getTemplateName();

  /**
   * Provides the path of the JADE template.
   * 
   * @return The absolute template path or an empty optional, if the page include provides only data.
   */
  Optional<Path> getTemplatePath();
}
