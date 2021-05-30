package com.github.gclaussn.ssg.builtin.action;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.conf.SiteConsole;
import com.github.gclaussn.ssg.conf.SiteProperty;
import com.github.gclaussn.ssg.plugin.SitePluginAction;

/**
 * Initializes a new site from a predefined template.
 */
public class InitAction implements SitePluginAction {

  private static final String TEMPLATE = "ssg.init.template";

  /** Locator for templates coming from classpath. */
  private static final String CLASSPATH_LOCATOR = "classpath:";

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private final Map<String, Object> properties;

    private Builder() {
      properties = new HashMap<>();
    }

    public void execute(Site site) {
      site.getPluginManager().execute(new InitAction(), properties);
    }

    public Builder template(String template) {
      properties.put(TEMPLATE, template);
      return this;
    }
  }

  protected SiteConsole console;

  /** Location of the template, used during initialization. */
  @SiteProperty(name = TEMPLATE)
  protected String template;

  private final ClassLoader classLoader;

  public InitAction() {
    classLoader = this.getClass().getClassLoader();
  }

  @Override
  public void execute(Site site) {
    console.log("Initializing site %s", site.getPath());

    if (template.startsWith(CLASSPATH_LOCATOR)) {
      executeFromClasspath(site);
    } else {
      executeFromPath(site);
    }
  }

  protected void executeFromClasspath(Site site) {
    String base = template.substring(InitAction.CLASSPATH_LOCATOR.length());

    console.log("Listing resources under %s", template);
    for (String resourceName : listResourceNames(base)) {
      String fullResourceName = new StringBuilder()
          .append(base)
          .append('/')
          .append(resourceName)
          .toString();

      Path target = site.getPath().resolve(resourceName);

      console.log("Copying resource: %s", fullResourceName);
      try (InputStream in = getResource(fullResourceName)) {
        Files.createDirectories(target.getParent());
        Files.copy(in, target);
      } catch (IOException e) {
        throw new RuntimeException(String.format("Site resource '%s' could not be copied", resourceName), e);
      }
    }
  }

  protected void executeFromPath(Site site) {
    Path templatePath = Paths.get(template);

    console.log("Listing resources under %s", template);
    List<Path> resources = listResources(templatePath);

    for (Path resource : resources) {
      String relativePath = templatePath.relativize(resource).toString();

      Path target = site.getPath().resolve(relativePath);

      console.log("Copying resource: %s", relativePath);
      try {
        Files.createDirectories(target.getParent());
        Files.copy(resource, target);
      } catch (IOException e) {
        throw new RuntimeException(String.format("Site resource '%s' could not be copied", resource), e);
      }
    }

    resources.clear();
  }

  protected InputStream getResource(String resourceName) {
    return classLoader.getResourceAsStream(resourceName);
  }

  protected List<Path> listResources(Path templatePath) {
    try {
      return Files.walk(templatePath).filter(Files::isRegularFile).collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException("Template resources could not be listed", e);
    }
  }

  protected List<String> listResourceNames(String template) {
    String resourceName = String.format("%s.txt", template);

    try (InputStream in = getResource(resourceName)) {
      return IOUtils.readLines(in, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException("Template resources could not be listed", e);
    }
  }
}
