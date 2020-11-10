package com.github.gclaussn.ssg.cli;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

public class CliTest {

  private ByteArrayOutputStream baos;

  private Cli cli;

  @Before
  public void setUp() {
    baos = new ByteArrayOutputStream();

    cli = new Cli(new PrintStream(baos));
  }

  @Test
  public void testHelp() {
    cli.run(new String[] {"--help"});

    assertThat(baos.toString(), startsWith("Usage: ssg"));
  }

  @Test
  public void testUnknownCommand() {
    cli.run(new String[] {"unknown"});

    assertThat(baos.toString(), startsWith("Unknown command 'unknown'"));
    assertThat(baos.toString(), containsString("Usage: ssg"));
  }

  @Test
  public void testGenerateHelp() {
    cli.run(new String[] {"generate", "--help"});

    assertThat(baos.toString(), containsString("Usage: generate"));
  }
}
