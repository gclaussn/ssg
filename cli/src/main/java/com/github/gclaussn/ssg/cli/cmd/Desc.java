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
    TypeDesc desc = site.getConfiguration().describe(typeName);
    
    builder = new CliOutputBuilder();

    if (desc.getName() != null) {
      builder.append(desc.getName());
      builder.newLine();
    }

    if (desc.getDocumentation() != null) {
      builder.newLine();
      builder.appendWrapped(desc.getDocumentation());
      builder.newLine();
    }

    if (!desc.getProperties().isEmpty()) {
      builder.newLine();
      builder.append("Properties:");

      builder.inc();
      desc.getProperties().forEach(this::append);
      builder.dec();
    }

    site.getConfiguration().getConsole().log(builder.toString());
  }

  protected void append(SitePropertyDesc desc) {
    builder.newLine();
    builder.indent();
    builder.append(desc.getName());
    builder.newLine();

    builder.inc();

    if (desc.getDocumentation() != null) {
      builder.indent();
      builder.appendWrapped(desc.getDocumentation());
      builder.newLine();
      builder.newLine();
    }

    append("Type", desc.getTypeName());
    append("Required", String.valueOf(desc.isRequired()));
    append("Default value", desc.getDefaultValue());
    append("Variable", desc.getVariableName());
    append("Variable value", desc.getVariable());

    builder.dec();
  }

  protected void append(String key, String value) {
    if (StringUtils.isNotBlank(value)) {
      builder.indent();
      builder.append(String.format("%s: %s", key, value));
      builder.newLine();
    }
  }
}
