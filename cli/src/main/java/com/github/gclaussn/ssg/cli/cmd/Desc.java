package com.github.gclaussn.ssg.cli.cmd;

import org.apache.commons.lang3.StringUtils;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.cli.AbstractCommand;
import com.github.gclaussn.ssg.cli.CliOutputBuilder;
import com.github.gclaussn.ssg.conf.SitePropertyDesc;
import com.github.gclaussn.ssg.conf.TypeDesc;

@Parameters(commandNames = "desc", commandDescription = "Describe type")
public class Desc extends AbstractCommand {

  @Parameter(description = "<type>", required = true)
  protected String typeName;

  private CliOutputBuilder builder;

  @Override
  public void run(Site site) {
    TypeDesc desc = site.getConf().describe(typeName);
    
    builder = new CliOutputBuilder();

    if (desc.getName() != null) {
      builder.append(desc.getName()).newLine();
    }

    if (desc.getDocumentation() != null) {
      builder.newLine().appendWrapped(desc.getDocumentation()).newLine();
    }

    if (!desc.getProperties().isEmpty()) {
      builder.newLine().append("Properties:");

      builder.inc();
      desc.getProperties().forEach(this::append);
      builder.dec();
    }

    site.getConf().getConsole().log(builder.toString());
  }

  protected void append(SitePropertyDesc desc) {
    builder.newLine().indent().append(desc.getName()).newLine();

    builder.inc();

    if (desc.getDocumentation() != null) {
      builder.indent().appendWrapped(desc.getDocumentation()).newLine().newLine();
    }

    append("Type", desc.getTypeName());
    append("Required", String.valueOf(desc.isRequired()));
    append("Default value", desc.getDefaultValue());
    append("Variable", desc.getVariableName());
    append("Variable value", desc.getVariable());
  }

  protected void append(String key, String value) {
    if (StringUtils.isNotBlank(value)) {
      builder.indent().append(String.format("%s: %s", key, value)).newLine();
    }
  }
}
