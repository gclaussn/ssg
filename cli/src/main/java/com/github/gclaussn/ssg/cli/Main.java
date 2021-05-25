package com.github.gclaussn.ssg.cli;

import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.PathConverter;

/**
 * Main command.
 */
public class Main {

  @Parameter(names = {"--base-path", "-b"}, description = "Base path")
  protected String basePath;

  @Parameter(names = {"--help"}, description = "Display this help", help = true)
  protected boolean help = false;

  @DynamicParameter(names = "-P", description = "Site properties")
  protected Map<String, String> properties = new HashMap<>();

  @Parameter(names = {"--site-path", "-s"}, description = "Path to the site", converter = PathConverter.class)
  protected Path sitePath;

  @Parameter(names = {"--verbose"}, description = "Show stacktrace")
  protected boolean verbose;

  private final PrintStream out;

  public Main(PrintStream out) {
    this.out = out;
  }

  public String getBasePath() {
    return basePath;
  }

  public PrintStream getOut() {
    return out;
  }

  public Map<String, Object> getProperties() {
    return new HashMap<>(properties);
  }

  /**
   * Returns the path to the site, specified as program argument.
   * 
   * @return The site's path or the current working directory, if not specified.
   */
  public Path getSitePath() {
    return sitePath != null ? sitePath : Paths.get(".").toAbsolutePath().normalize();
  }

  public boolean isHelp() {
    return help;
  }

  public boolean isVerbose() {
    return verbose;
  }
}
