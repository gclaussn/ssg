package com.github.gclaussn.ssg;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.github.gclaussn.ssg.data.PageData;
import com.github.gclaussn.ssg.data.PageDataSelectorBean;

/**
 * A page within a static site that can be generated.
 */
public interface Page extends Source {

  /**
   * Generates the page.<br />
   * If the page is skipped or rejected, it cannot be generated - in this case an
   * {@link SiteException} is thrown.
   * 
   * @return An error, if the generation failed. Otherwise an empty optional.
   */
  Optional<SiteError> generate();

  /**
   * Returns the data, specified within the page's YAML file.
   * 
   * @return The page data.
   */
  PageData getData();

  /**
   * Provides the specified data selectors, which are executed when the page is generated.
   * 
   * @return A list of {@link PageDataSelectorBean}s.
   * 
   * @see SiteGenerator#compilePageData(String)
   */
  List<PageDataSelectorBean> getDataSelectors();

  /**
   * Returns the path to the page's model (YAML) file.
   * 
   * @return The model path, if the file exists. Otherwise an empty optional.
   */
  Optional<Path> getModelPath();

  /**
   * Returns the output name of the generated page HTML file, relative to the site's base path - e.g.
   * "news/2020-07-23-xyz.html". If not specified diffently within the model (via "out" field), the
   * output name will be the page ID plus HTML file extension.
   * 
   * @return The page's output path.
   */
  String getOutputName();

  /**
   * Returns the output path of the generated page HTM file.
   * 
   * @return The absolute output path.
   * 
   * @see Site#getOutputPath()
   */
  Path getOutputPath();

  /**
   * Provides all includes, that are used by the page.
   * 
   * @return A set, containing all used {@link PageInclude}s.
   */
  Set<PageInclude> getPageIncludes();

  /**
   * Returns the set, the page is related to.
   * 
   * @return The related {@link PageSet} or an empty optional, if the page is not part of a set.
   */
  Optional<PageSet> getPageSet();

  /**
   * Provides the ID of the {@link PageFilterBean}, if the page is part of a set and has been rejected
   * by one of the set's filters.
   * 
   * @return The filter bean ID or an empty optional.
   */
  Optional<String> getRejectedBy();

  /**
   * Returns the ID of the page without the page set ID part - e.g. "posts/2020-10-25-xyz" will become
   * "2020-10-25-xyz", if the ID of the page set is "posts".
   * 
   * @return The sub ID or an empty optional, if the page is not part of a set.
   */
  Optional<String> getSubId();

  /**
   * Provides the name of the JADE template, that is used to render the page. The template must exist
   * within the site's source path.
   * 
   * @return The JADE template name.
   */
  String getTemplateName();

  /**
   * Provides the path of the JADE template, that is used to render the page.
   * 
   * @return The absolute template path.
   */
  Path getTemplatePath();

  /**
   * Returns the URL of the page without HTML file extension - e.g. "/blog" or
   * "/news/2020-07-19-xyz". If the output path is "index.html", the URL becomes "/".
   * 
   * @return The absolute page URL.
   */
  String getUrl();

  /**
   * Determines if the page is rejected by a {@link PageFilter}. A rejected page cannot be generated.
   * 
   * @return {@code true}, if the page is rejected. Otherwise {@code false}.
   */
  boolean isRejected();

  /**
   * Determines if the page is skipped during site generation. A skipped page can still provides its
   * data.
   * 
   * @return {@code true}, if the page is skipped during generation. Otherwise {@code false}.
   */
  boolean isSkipped();
}
