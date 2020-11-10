package com.github.gclaussn.ssg.builtin;

import java.util.LinkedList;
import java.util.List;

import com.github.gclaussn.ssg.PageFilter;
import com.github.gclaussn.ssg.PageProcessor;
import com.github.gclaussn.ssg.SiteBuilder;
import com.github.gclaussn.ssg.builtin.goal.CopyOutputGoal;
import com.github.gclaussn.ssg.builtin.goal.GenerateGoal;
import com.github.gclaussn.ssg.builtin.goal.InitGoal;
import com.github.gclaussn.ssg.builtin.selector.PageSetAggregator;
import com.github.gclaussn.ssg.builtin.selector.PageSetSelector;
import com.github.gclaussn.ssg.data.PageDataSelector;
import com.github.gclaussn.ssg.plugin.SitePlugin;
import com.github.gclaussn.ssg.plugin.SitePluginGoal;

/**
 * Default site plugin, which provides builtin implementations of {@link PageProcessor},
 * {@link PageFilter} and {@link PageDataSelector} as well as default goals to execute.
 */
public class DefaultPlugin implements SitePlugin {

  @Override
  public List<SitePluginGoal> getGoals() {
    List<SitePluginGoal> goals = new LinkedList<>();
    goals.add(new CopyOutputGoal());
    goals.add(new GenerateGoal());
    goals.add(new InitGoal());

    return goals;
  }

  @Override
  public void preBuild(SiteBuilder builder) {
    builder.addPageDataSelectorType(PageSetAggregator.class);
    builder.addPageDataSelectorType(PageSetSelector.class);
    builder.addPageFilterType(DateFilter.class);
    builder.addPageFilterType(PublishedFilter.class);
    builder.addPageProcessorType(DateProcessor.class);
  }
}
