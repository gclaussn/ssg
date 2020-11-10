package com.github.gclaussn.ssg.cli.cmd;

import java.util.HashMap;
import java.util.Map;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.builtin.goal.CopyOutputGoal;
import com.github.gclaussn.ssg.cli.AbstractCmd;

@Parameters(commandNames = "cp", commandDescription = "Copy the site's output")
public class CopyOutput extends AbstractCmd {

  @Parameter(description = "<target>", required = true)
  protected String target;

  @Override
  public int run(Site site, JCommander jc) {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put(CopyOutputGoal.TARGET, target);

    return site.execute("default:copy-output", properties);
  }
}
