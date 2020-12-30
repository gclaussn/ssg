package com.github.gclaussn.ssg.builtin.goal;

import java.util.Iterator;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteOutput;
import com.github.gclaussn.ssg.conf.SiteConsole;
import com.github.gclaussn.ssg.plugin.SitePluginGoal;

public class LsGoal implements SitePluginGoal {

  protected SiteConsole console;

  @Override
  public void execute(Site site) {
    Iterator<SiteOutput> it = site.serve().iterator();
    while (it.hasNext()) {
      SiteOutput output = it.next();

      console.log(output.getPath());
    }
  }
}
