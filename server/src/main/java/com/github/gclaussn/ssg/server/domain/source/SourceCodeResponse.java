package com.github.gclaussn.ssg.server.domain.source;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

class SourceCodeResponse implements StreamingOutput {

  private final Path path;

  private int from;
  private int to;

  SourceCodeResponse(Path path, int from, int to) {
    this.path = path;
    this.from = from;
    this.to = to;
  }

  @Override
  public void write(OutputStream output) throws IOException, WebApplicationException {
    if (!Files.isRegularFile(path)) {
      // YAML or JADE source file does not exist
      return;
    }

    List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

    if (from <= 0) {
      from = 1;
    }
    if (to < from || to > lines.size()) {
      to = lines.size();
    }

    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));

    for (int i = from; i <= to; i++) {
      String line = lines.get(i - 1);

      writer.write(line);
      writer.newLine();
    }

    writer.flush();
  }
}
