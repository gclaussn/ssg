package com.github.gclaussn.ssg;

import static com.github.gclaussn.ssg.file.SiteFileType.YAML;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.github.gclaussn.ssg.conf.SiteConf;
import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.event.SiteEventStore;
import com.github.gclaussn.ssg.file.SiteFileEventListener;
import com.github.gclaussn.ssg.file.SiteFileType;
import com.github.gclaussn.ssg.impl.SiteBuilderImpl;
import com.github.gclaussn.ssg.npm.NodePackageSpec;
import com.github.gclaussn.ssg.plugin.SitePluginManager;

/**
 * Static site representation, which loades pages and page sets that are defined in its
 * {@code site.yaml} file.<br>
 * <br>
 * A site can also be driven by file events (create, modify, delete) - see
 * {@link SiteFileEventListener}. It handles events regarding the {@code site.yaml} file and all
 * source files ({@code src/}) of type {@link SiteFileType#YAML}, {@link SiteFileType#JADE} and
 * {@link SiteFileType#MD}.
 */
public interface Site extends SiteFileEventListener, AutoCloseable {

  /** Name of the site model (YAML) file. */
  static final String MODEL_NAME = YAML.appendTo("site");

  /** Name of the folder, that is used as target location for the generated HTML output. */
  static final String OUTPUT = "out";
  /** Name of the folder, that provides public assets like scripts, style sheets and images. */
  static final String PUBLIC = "pub";
  /** Name of the source folder, containing YAML and JADE files. */
  static final String SOURCE = "src";

  /**
   * Creates a new site builder.
   * 
   * @return The newly created site builder.
   */
  static SiteBuilder builder() {
    return new SiteBuilderImpl();
  }

  /**
   * Creates a new site from the given path, using a default configuration.<br>
   * Please note: {@link #load()} must be called to load the site initially.
   * 
   * @param sitePath Path to a directory with a {@code site.yaml} file.
   * 
   * @return The newly create site, not loaded yet.
   * 
   * @see #load()
   */
  static Site from(Path sitePath) {
    return builder().build(sitePath);
  }

  /**
   * Analyzes, which pages use the page include with the given ID.<br>
   * If the page include does not exist, an {@link SiteException} is thrown.
   * 
   * @param pageIncludeId The ID of a specific {@link PageInclude}.
   * 
   * @return A list, containing all using pages.
   */
  List<Page> analyzePageIncludeUsage(String pageIncludeId);

  /**
   * Analyzes, which pages use the page set with the given ID via data selectors.<br>
   * If the page set does not exist, an {@link SiteException} is thrown.
   * 
   * @param pageSetId The ID of a specific {@link PageSet}.
   * 
   * @return A list, containing all using pages.
   */
  List<Page> analyzePageSetUsage(String pageSetId);

  /**
   * Closes the site and destroys all plugins and beans.
   */
  @Override
  void close();

  /**
   * Generates the output of all known pages.
   * 
   * @return A list with {@link SiteError}s that occurred during generation.
   * 
   * @see SiteGenerator#generate()
   */
  List<SiteError> generate();

  /**
   * Returns the configuration, used by the site.
   * 
   * @return The site's configuration.
   */
  SiteConf getConfiguration();

  /**
   * Provides the underlying {@link SiteEvent} store.
   * 
   * @return The event store.
   */
  SiteEventStore getEventStore();

  /**
   * Returns the generator, which is used to generate pages.
   * 
   * @return The site generator.
   */
  SiteGenerator getGenerator();

  /**
   * Gets the node package specification, if defined in {@code site.yaml} under property "node".
   * 
   * @return The specified node packages. Otherwise an empty optional.
   */
  Optional<NodePackageSpec> getNodePackages();

  /**
   * Provides the path to the directory, where the generator output is written to.
   * 
   * @return The site's output directory.
   */
  Path getOutputPath();

  Page getPage(String pageId);

  Set<Page> getPages();

  PageInclude getPageInclude(String pageIncludeId);

  Set<PageInclude> getPageIncludes();

  PageSet getPageSet(String pageSetId);

  Set<PageSet> getPageSets();

  /**
   * Provides the path to the site's directory.
   * 
   * @return The site path.
   */
  Path getPath();

  /**
   * Gets the plugin manager, which is able to list the registered plugins and execute actions,
   * provided by the plugins, against the site.
   * 
   * @return The site's plugin manager.
   */
  SitePluginManager getPluginManager();

  /**
   * Provides the path to the directory, that contains public files like e.g. images or CSS files.
   * 
   * @return The site's public directory.
   */
  Path getPublicPath();

  /**
   * Identifies the source, using the given ID.<br>
   * If there is no such source, the type will be {@link SourceType#UNKNOWN}.
   * 
   * @param id The ID of the source to identify.
   * 
   * @return The source of a specific type.
   */
  Source getSource(String id);

  /**
   * Provides the path to the directory, that contains the sources (YAML and JADE files) of all
   * {@link Page}s, {@link PageSet}s and {@link PageInclude}s.
   * 
   * @return The site's source directory.
   */
  Path getSourcePath();

  /**
   * Determines if the site has a page with the given ID.
   * 
   * @param pageId A specific page ID - e.g. "index"
   * 
   * @return {@code true}, if the page exists. Otherwise {@code false}.
   */
  boolean hasPage(String pageId);

  /**
   * Determines if the site has a page include with the given ID.
   * 
   * @param pageIncludeId A specific page include ID - e.g. "common/mixins"
   * 
   * @return {@code true}, if the page include exists. Otherwise {@code false}.
   */
  boolean hasPageInclude(String pageIncludeId);

  /**
   * Determines if the site has a page set with the given ID.
   * 
   * @param pageSetId A specific page set ID - e.g. "posts"
   * 
   * @return {@code true}, if the page set exists. Otherwise {@code false}.
   */
  boolean hasPageSet(String pageSetId);

  /**
   * Loads the site, based on its {@code site.yaml} file.<br>
   * This method can also be called to reload the site at any given time.
   * 
   * @return A list with {@link SiteError}s that occurred during loading.
   */
  List<SiteError> load();

  /**
   * Serves all files that are part of the generated site. This includes output files (generated
   * pages) as well as public files and Node.js modules, if present.
   * 
   * @return A stream serving all {@link SiteOutput}s.
   */
  Stream<SiteOutput> serve();

  SiteOutput serve(String requestUri);
}
