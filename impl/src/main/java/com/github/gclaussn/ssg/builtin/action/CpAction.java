package com.github.gclaussn.ssg.builtin.action;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteOutput;
import com.github.gclaussn.ssg.conf.SiteConsole;
import com.github.gclaussn.ssg.conf.SiteProperty;
import com.github.gclaussn.ssg.plugin.SitePluginAction;

public class CpAction implements SitePluginAction {

  private static final String TARGET = "ssg.cp.target";

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private final Map<String, Object> properties;

    private Builder() {
      properties = new HashMap<>();
    }

    public void execute(Site site) {
      site.getPluginManager().execute(new CpAction(), properties);
    }

    public Builder target(String target) {
      properties.put(TARGET, target);
      return this;
    }
  }

  protected SiteConsole console;

  /** Target directory for the site's output. */
  @SiteProperty(name = TARGET)
  protected String target;

  @Override
  public void execute(Site site) {
    console.log("Copying output of site %s to %s", site.getPath(), target);

    Path targetPath = Paths.get(target);

    Iterator<SiteOutput> it = site.serve().iterator();
    while (it.hasNext()) {
      SiteOutput output = it.next();

      Path filePath = targetPath.resolve(output.getName());

      console.log("Copying output: %s", output.getName());
      try {
        Files.createDirectories(filePath.getParent());
        Files.copy(output.getFilePath(), filePath, StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e) {
        throw new RuntimeException(String.format("Site output '%s' could not be copied", output.getName()), e);
      }
    }
  }
}
