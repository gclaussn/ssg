package com.github.gclaussn.ssg.builtin.action;

import static com.github.gclaussn.ssg.test.CustomMatcher.isDirectory;
import static com.github.gclaussn.ssg.test.CustomMatcher.isFile;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Rule;
import org.junit.Test;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.npm.NodePackageManager;
import com.github.gclaussn.ssg.test.SiteRule;

public class InstallActionTest {

  @Rule
  public SiteRule rule = new SiteRule(this);

  @Test
  public void testExecute() {
    Site site = rule.init();

    site.getPluginManager().execute("install");

    assertThat(site.getPath().resolve(NodePackageManager.NODE_MODULES), isDirectory());
    assertThat(site.getPath().resolve(NodePackageManager.NODE_MODULES).resolve("jquery"), isDirectory());

    // check some known files
    assertThat(site.getPath().resolve(NodePackageManager.NODE_MODULES).resolve("jquery/dist/jquery.min.js"), isFile());
    assertThat(site.getPath().resolve(NodePackageManager.NODE_MODULES).resolve("jquery/package.json"), isFile());
    assertThat(site.getPath().resolve(NodePackageManager.NODE_MODULES).resolve("jquery/src/ajax.js"), isFile());

    // download directory is deleted
    assertThat(site.getPath().resolve(NodePackageManager.NODE_MODULES).resolve(".dl"), not(isDirectory()));
  }
}
