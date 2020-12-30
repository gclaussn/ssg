package com.github.gclaussn.ssg.cli;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

public class CliOutputBuilder {

  private final StringBuilder sb;

  private int indent;

  public CliOutputBuilder() {
    sb = new StringBuilder(256);
    indent = 0;
  }

  public CliOutputBuilder append(String text) {
    sb.append(text);
    return this;
  }

  public CliOutputBuilder appendWrapped(String text) {
    sb.append(WordUtils.wrap(text, 100 - indent, "\n" + StringUtils.SPACE.repeat(indent), true));
    return this;
  }

  public CliOutputBuilder dec() {
    indent -= 2;
    return this;
  }

  public CliOutputBuilder inc() {
    indent += 2;
    return this;
  }

  public CliOutputBuilder indent() {
    sb.append(StringUtils.SPACE.repeat(indent));
    return this;
  }

  public CliOutputBuilder newLine() {
    if (sb.length() > 0) {
      sb.append('\n');
    }
    return this;
  }

  @Override
  public String toString() {
    return sb.toString();
  }
}
