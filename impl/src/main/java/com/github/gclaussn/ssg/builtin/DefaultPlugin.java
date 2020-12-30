package com.github.gclaussn.ssg.builtin;

import com.github.gclaussn.ssg.SiteBuilder;
import com.github.gclaussn.ssg.builtin.goal.CpGoal;
import com.github.gclaussn.ssg.builtin.goal.GenerateGoal;
import com.github.gclaussn.ssg.builtin.goal.InitGoal;
import com.github.gclaussn.ssg.builtin.goal.LsGoal;
import com.github.gclaussn.ssg.builtin.selector.PageSetAggregator;
import com.github.gclaussn.ssg.builtin.selector.PageSetSelector;
import com.github.gclaussn.ssg.plugin.SitePlugin;

/**
 * Default site plugin, which provides builtin implementations as well as default goals to execute.
 */
public class DefaultPlugin implements SitePlugin {

  @Override
  public void preBuild(SiteBuilder builder) {
    builder.addPageDataSelector(PageSetAggregator.class);
    builder.addPageDataSelector(PageSetSelector.class);
    builder.addPageFilter(DateFilter.class);
    builder.addPageFilter(PublishedFilter.class);
    builder.addPageProcessor(DateProcessor.class);

    // goals
    builder.addPluginGoal(CpGoal.class);
    builder.addPluginGoal(GenerateGoal.class);
    builder.addPluginGoal(InitGoal.class);
    builder.addPluginGoal(LsGoal.class);
  }
}
