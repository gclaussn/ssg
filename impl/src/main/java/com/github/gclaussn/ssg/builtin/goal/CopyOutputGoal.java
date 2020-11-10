package com.github.gclaussn.ssg.builtin.goal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteOutput;
import com.github.gclaussn.ssg.conf.SiteProperty;
import com.github.gclaussn.ssg.plugin.SitePluginGoal;

public class CopyOutputGoal implements SitePluginGoal {

  private static final Logger LOGGER = LoggerFactory.getLogger(CopyOutputGoal.class);

  public static final String TARGET = "ssg.cp.target";

  /** Copy target directory for the site's output. */
  @SiteProperty(name = TARGET)
  protected String target;

  @Override
  public int execute(Site site) {
    LOGGER.info("Copying output of site {} to {}", site.getPath(), target);

    Path targetPath = Paths.get(target);

    Iterator<SiteOutput> it = site.serve().iterator();
    while (it.hasNext()) {
      SiteOutput output = it.next();

      Path filePath = targetPath.resolve(output.getName());

      LOGGER.info("Copying output: {}", output.getName());
      try {
        Files.createDirectories(filePath.getParent());
        Files.copy(output.getFilePath(), filePath, StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e) {
        throw new RuntimeException(String.format("Site output '%s' could not be copied", output.getName()), e);
      }
    }

    return SC_SUCCESS;
  }
}
