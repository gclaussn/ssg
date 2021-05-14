package com.github.gclaussn.ssg.builtin;

import com.github.gclaussn.ssg.SiteBuilder;
import com.github.gclaussn.ssg.builtin.action.CpAction;
import com.github.gclaussn.ssg.builtin.action.GenerateAction;
import com.github.gclaussn.ssg.builtin.action.InitAction;
import com.github.gclaussn.ssg.builtin.action.InstallAction;
import com.github.gclaussn.ssg.builtin.action.LsAction;
import com.github.gclaussn.ssg.builtin.selector.PageSetAggregator;
import com.github.gclaussn.ssg.builtin.selector.PageSetMapper;
import com.github.gclaussn.ssg.builtin.selector.PageSetSelector;
import com.github.gclaussn.ssg.plugin.SitePlugin;

/**
 * Default site plugin, which provides builtin implementations as well as default actions to execute.
 */
public class DefaultPlugin implements SitePlugin {

  @Override
  public void preBuild(SiteBuilder builder) {
    // register types
    builder.addPageDataSelector(PageSetAggregator.class);
    builder.addPageDataSelector(PageSetMapper.class);
    builder.addPageDataSelector(PageSetSelector.class);
    builder.addPageFilter(DateFilter.class);
    builder.addPageProcessor(DateProcessor.class);

    // register actions
    builder.addPluginAction(CpAction.class);
    builder.addPluginAction(GenerateAction.class);
    builder.addPluginAction(InitAction.class);
    builder.addPluginAction(InstallAction.class);
    builder.addPluginAction(LsAction.class);
  }
}
