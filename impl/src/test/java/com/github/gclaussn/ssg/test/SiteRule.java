package com.github.gclaussn.ssg.test;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.rules.TemporaryFolder;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.builtin.action.InitAction;

public class SiteRule extends TemporaryFolder {

  private final Object testInstance;

  public SiteRule(Object testInstance) {
    super(Paths.get("./target").toFile());

    this.testInstance = testInstance;
  }

  public Path getPath() {
    return getRoot().toPath();
  }

  public String getResourceName() {
    return getResourceName(testInstance.getClass());
  }

  public String getResourceName(Class<?> testClassName) {
    return testClassName.getName().replace('.', '/');
  }

  public Site init() {
    Site site = Site.from(getPath());

    InitAction.builder().template(String.format("./src/test/resources/%s", getResourceName())).execute(site);

    return site;
  }
}
