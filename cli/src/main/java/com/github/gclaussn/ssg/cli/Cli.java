package com.github.gclaussn.ssg.cli;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.MissingCommandException;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.internal.DefaultConsole;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteBuilder;
import com.github.gclaussn.ssg.cli.cmd.Cp;
import com.github.gclaussn.ssg.cli.cmd.Desc;
import com.github.gclaussn.ssg.cli.cmd.Exec;
import com.github.gclaussn.ssg.cli.cmd.Generate;
import com.github.gclaussn.ssg.cli.cmd.Init;
import com.github.gclaussn.ssg.cli.cmd.Install;
import com.github.gclaussn.ssg.cli.cmd.Ls;
import com.github.gclaussn.ssg.cli.cmd.Plugins;
import com.github.gclaussn.ssg.cli.cmd.Server;
import com.github.gclaussn.ssg.plugin.SitePluginException;

/**
 * Main class, running CLI commands.
 */
public class Cli {

  public static void main(String[] args) {
    System.exit(new Cli().run(args));
  }

  private static final String PROGRAM_NAME = "ssg";

  private final Main main;

  /** List of available commands. */
  private final List<AbstractCommand> commands;

  private final JCommander jc;

  public Cli() {
    this(System.out);
  }

  protected Cli(PrintStream ps) {
    main = new Main(ps);

    commands = new LinkedList<>();
    commands.add(new Cp());
    commands.add(new Desc());
    commands.add(new Exec());
    commands.add(new Generate());
    commands.add(new Init());
    commands.add(new Install());
    commands.add(new Ls());
    commands.add(new Plugins());
    commands.add(new Server());

    JCommander.Builder builder = JCommander.newBuilder().console(new DefaultConsole(ps)).addObject(main);

    commands.forEach(builder::addCommand);

    jc = builder.build();
  }

  protected Site buildSite(AbstractCommand command) {
    SiteBuilder builder = Site.builder();

    // call preBuild hook
    command.preBuild(builder, main);

    return builder.setPropertyMap(main.getProperties()).build(main.getSitePath());
  }

  protected Optional<AbstractCommand> findCommand(String commandName) {
    if (commandName == null) {
      return Optional.empty();
    }

    AbstractCommand command = (AbstractCommand) jc.findCommandByAlias(commandName).getObjects().get(0);
    return Optional.of(command);
  }

  public int run(String[] args) {
    try {
      jc.parse(args);
    } catch (MissingCommandException e) {
      jc.getConsole().println(String.format("Unknown command '%s'\n", e.getUnknownCommand()));
    } catch (ParameterException e) {
      jc.getConsole().println(String.format("Invalid parameters: %s\n", e.getMessage()));

      main.help = true;
    }

    Optional<AbstractCommand> command = findCommand(jc.getParsedCommand());
    if (!command.isPresent()) {
      jc.setProgramName(PROGRAM_NAME);
      jc.usage();
      return 0;
    }

    return run(command.get());
  }

  protected void log(SitePluginException e) {
    if (main.isVerbose()) {
      e.printStackTrace();
    } else if (e.getCause() != null) {
      jc.getConsole().println(e.getCause().getMessage());
    } else {
      jc.getConsole().println(e.getMessage());
    }
  }

  protected int run(AbstractCommand command) {
    if (main.isHelp() || command.isHelp()) {
      jc.getUsageFormatter().usage(jc.getParsedCommand());
      return 1;
    }

    try (Site site = buildSite(command)) {
      command.run(site);
    } catch (SitePluginException e) {
      log(e);
      return e.getStatusCode();
    } catch (RuntimeException e) {
      e.printStackTrace();
      return 1;
    }

    return 0;
  }
}
