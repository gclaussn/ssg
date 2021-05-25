package com.github.gclaussn.ssg.cli;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

public class CliTest {

  private ByteArrayOutputStream out;

  private Cli cli;

  @Before
  public void setUp() {
    out = new ByteArrayOutputStream();

    cli = new Cli(new PrintStream(out));
  }

  @Test
  public void testHelp() {
    cli.run(new String[] {"--help"});

    assertThat(out.toString(), startsWith("Usage: ssg"));
  }

  @Test
  public void testUnknownCommand() {
    cli.run(new String[] {"unknown"});

    assertThat(out.toString(), startsWith("Unknown command 'unknown'"));
    assertThat(out.toString(), containsString("Usage: ssg"));
  }

  @Test
  public void testGenerateHelp() {
    cli.run(new String[] {"generate", "--help"});

    assertThat(out.toString(), containsString("Usage: generate"));
  }

  @Test
  public void testPlugins() {
    cli.run(new String[] {"plugins"});

    assertThat(out.toString(), startsWith("com.github.gclaussn.ssg.builtin.DefaultPlugin"));
    assertThat(out.toString(), containsString("com.github.gclaussn.ssg.server.ServerPlugin"));
  }

  @Test
  public void testDesc() {
    cli.run(new String[] {"desc", "com.github.gclaussn.ssg.builtin.action.CpAction"});

    assertThat(out.toString(), startsWith("Copy"));
  }
}
