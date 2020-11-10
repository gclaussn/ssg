package com.github.gclaussn.ssg.builtin.goal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.plugin.SitePluginGoal;

class InitFromClasspath implements SitePluginGoal {

  private static final Logger LOGGER = LoggerFactory.getLogger(InitFromClasspath.class);

  private final String template;

  private final ClassLoader classLoader;

  InitFromClasspath(String template) {
    this.template = template;

    classLoader = this.getClass().getClassLoader();
  }

  @Override
  public int execute(Site site) {
    String base = template.substring(InitGoal.CLASSPATH_LOCATOR.length());

    LOGGER.info("Listing resources under {}", template);
    for (String fileName : listFileNames(base)) {
      String resourceName = new StringBuilder()
          .append(base)
          .append('/')
          .append(fileName)
          .toString();

      Path target = site.getPath().resolve(fileName);

      LOGGER.info("Copying resource: {}", fileName);
      try (InputStream in = getResource(resourceName)) {
        Files.createDirectories(target.getParent());
        Files.copy(in, target);
      } catch (IOException e) {
        throw new RuntimeException(String.format("Site resource '%s' could not be copied", resourceName), e);
      }
    }

    return SC_SUCCESS;
  }

  protected InputStream getResource(String resourceName) {
    return classLoader.getResourceAsStream(resourceName);
  }

  protected List<String> listFileNames(String template) {
    String resourceName = String.format("%s.txt", template);

    try (InputStream in = getResource(resourceName)) {
      return IOUtils.readLines(in, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException("Site resources could not be listed", e);
    }
  }
}
