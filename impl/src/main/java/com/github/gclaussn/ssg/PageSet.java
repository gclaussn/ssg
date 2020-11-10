package com.github.gclaussn.ssg;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.github.gclaussn.ssg.data.PageData;

/**
 * A page set, provides default data and a template for contained pages that are part of the page
 * set.
 */
public interface PageSet extends Source {

  /**
   * Generates all pages of the page set.<br />
   * If the page set is skipped, it's pages cannot be generated - in this case an
   * {@link SiteException} is thrown.
   * 
   * @return A list with errors, that occurred during generation.
   */
  List<SiteError> generate();

  /**
   * Returns an explicit base, which is used to build the output name of a page - e.g. if the base
   * path is "news" and the page ID is "posts/my-post", the output name of the page will be
   * "news/my-post.html"
   * 
   * @return The base path or an empty optional.
   */
  Optional<String> getBasePath();

  /**
   * Returns the data, specified within the page set's YAML file.
   * 
   * @return The page set data.
   */
  PageData getData();

  /**
   * Returns the path to the page set's model (YAML) file.
   * 
   * @return The model path, if the file exists. Otherwise an empty optional.
   */
  Optional<Path> getModelPath();

  /**
   * Provides all pages, that are part of the page set.
   * 
   * @return A set, containing the {@link Page}s.
   */
  Set<Page> getPages();

  /**
   * Provides all includes, that are used by the page set.
   * 
   * @return A set, containing all used {@link PageInclude}s.
   */
  Set<PageInclude> getPageIncludes();

  /**
   * Provides the name of the JADE template, that is used to render the pages of the set. The template
   * must exist within the site's source path.
   * 
   * @return The JADE template name.
   */
  String getTemplateName();

  /**
   * Provides the path of the JADE template, that is used to render the pages of the set.
   * 
   * @return The absolute template path.
   */
  Path getTemplatePath();

  /**
   * Determines if the page set is skipped during site generation.
   * 
   * @return {@code true}, if the set page is skipped during generation. Otherwise {@code false}.
   */
  boolean isSkipped();
}
