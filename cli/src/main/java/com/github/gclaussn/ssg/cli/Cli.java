package com.github.gclaussn.ssg.cli;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.MissingCommandException;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.internal.DefaultConsole;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.cli.cmd.CopyOutput;
import com.github.gclaussn.ssg.cli.cmd.Execute;
import com.github.gclaussn.ssg.cli.cmd.Generate;
import com.github.gclaussn.ssg.cli.cmd.Init;
import com.github.gclaussn.ssg.cli.cmd.ListOutput;
import com.github.gclaussn.ssg.cli.cmd.Server;
import com.github.gclaussn.ssg.plugin.SitePluginException;

/**
 * Main class, running CLI commands.
 */
public class Cli {

  public static void main(String[] args) {
    System.exit(new Cli().run(args));
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(Cli.class);

  private final Main main;

  /** List of available commands. */
  private final List<AbstractCmd> commands;

  private final JCommander jc;

  public Cli() {
    this(System.out);
  }

  protected Cli(PrintStream ps) {
    main = new Main();

    commands = new LinkedList<>();
    commands.add(new CopyOutput());
    commands.add(new Execute());
    commands.add(new Generate());
    commands.add(new Init());
    commands.add(new ListOutput());
    commands.add(new Server());

    JCommander.Builder builder = JCommander.newBuilder().console(new DefaultConsole(ps)).addObject(main);

    commands.forEach(builder::addCommand);

    jc = builder.build();
  }

  protected Optional<AbstractCmd> findCommand(String commandName) {
    if (commandName == null) {
      return Optional.empty();
    }

    AbstractCmd command = (AbstractCmd) jc.findCommandByAlias(commandName).getObjects().get(0);
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

    Optional<AbstractCmd> command = findCommand(jc.getParsedCommand());
    if (!command.isPresent()) {
      jc.setProgramName(CliConstants.PROGRAM_NAME);
      jc.usage();
      return CliConstants.SC_ERROR;
    }

    return run(command.get());
  }

  protected int run(AbstractCmd command) {
    if (main.isHelp() || command.isHelp()) {
      jc.getUsageFormatter().usage(jc.getParsedCommand());
      return CliConstants.SC_SUCCESS;
    }

    try (Site site = command.build(main)) {
      return command.run(site, jc);
    } catch (SitePluginException e) {
      LOGGER.error("Command failed", e);
      return CliConstants.SC_ERROR;
    }
  }
}
