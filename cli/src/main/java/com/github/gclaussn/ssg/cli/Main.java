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

  @Parameter(names = {"--help"}, description = "Display this help", help = true)
  protected boolean help = false;

  @DynamicParameter(names = "-P", description = "Site properties")
  protected Map<String, String> properties = new HashMap<>();

  @Parameter(names = {"--site-path", "-s"}, description = "Site path", converter = PathConverter.class)
  protected Path sitePath;

  @Parameter(names = {"--verbose"}, description = "Show stacktraces")
  protected boolean verbose;

  private final PrintStream ps;

  public Main(PrintStream ps) {
    this.ps = ps;
  }

  public PrintStream getPrintStream() {
    return ps;
  }

  public Map<String, Object> getProperties() {
    return new HashMap<>(properties);
  }

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
