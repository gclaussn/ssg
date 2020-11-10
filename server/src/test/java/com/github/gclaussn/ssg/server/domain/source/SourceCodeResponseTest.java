package com.github.gclaussn.ssg.server.domain.source;

import static com.github.gclaussn.ssg.file.SiteFileType.YAML;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import com.github.gclaussn.ssg.Site;

public class SourceCodeResponseTest {

  private Path path;

  private ByteArrayOutputStream baos;

  @Before
  public void setUp() {
    Site site = Site.from(Paths.get("src/test/resources/sites/sample"));

    path = site.getSourcePath().resolve(YAML.appendTo("test"));

    baos = new ByteArrayOutputStream();
  }

  @Test
  public void testWrite() throws IOException {
    new SourceCodeResponse(path, 1, -1).write(baos);

    String[] lines = baos.toString(StandardCharsets.UTF_8).split("\\n");
    assertThat(lines.length, is(6));
  }
}
