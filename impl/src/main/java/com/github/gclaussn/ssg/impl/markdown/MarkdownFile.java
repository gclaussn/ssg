package com.github.gclaussn.ssg.impl.markdown;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

/**
 * Markdown file, including YAML front matter support.
 */
public class MarkdownFile {

  private static final int BUFFER_SIZE = 8192;

  private static final int SEPARATOR_LENGTH = 4;

  /**
   * Reads the Markdown file from the given file path.
   * 
   * @param filePath The path to an existing Markdown file.
   * 
   * @return The read Markdown file, which provides Markdown code and YAML, if a front matter exists.
   * 
   * @throws IOException If any I/O error occurs.
   * 
   * @see #getMarkdown()
   * @see #getYaml()
   * @see #hasYaml()
   */
  public static MarkdownFile from(Path filePath) throws IOException {
    MarkdownFile markdownFile = new MarkdownFile(filePath);
    markdownFile.read();

    return markdownFile;
  }

  private final Path filePath;

  private String markdown;
  private String yaml;

  private int i;
  /** The length of the front matter, determined during {@link #read(InputStream)}. */
  private int l;

  protected MarkdownFile(Path filePath) {
    this.filePath = filePath;
  }

  /**
   * Returns the Markdown code, which was read from the file.
   * 
   * @return The Markdown code.
   */
  public String getMarkdown() {
    return markdown;
  }

  /**
   * Returns the content of the YAML front matter, if it exists.
   * 
   * @return The YAML content or {@code null}.
   */
  public String getYaml() {
    return yaml;
  }

  /**
   * Checks if the Markdown file has a YAML front matter or not.
   * 
   * @return {@code true}, if a front matter exists. Otherwise {@code false}.
   */
  public boolean hasYaml() {
    if (yaml == null) {
      return false;
    } else {
      return yaml.chars().anyMatch(Character::isLetterOrDigit);
    }
  }

  private void read() throws IOException {
    read(Files.newInputStream(filePath));
  }

  protected void read(InputStream in) throws IOException {
    try (BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8), BUFFER_SIZE)) {
      // mark beginning, to be able to reset
      // if front matter does not exist
      r.mark(SEPARATOR_LENGTH);

      i = 0;
      scan(r.read());
      scan(r.read());
      scan(r.read());
      scan(r.read());

      if (i != SEPARATOR_LENGTH) {
        // front matter does not exist
        r.reset();
        readMarkdown(r);
        return;
      }

      // mark after 4 characters
      r.mark(BUFFER_SIZE);

      i = 0;
      l = 0;
      while (i != -1) {
        scan(r.read());

        if (i == 4) {
          // front matter exists
          break;
        }
      }

      if (i == -1) {
        // YAML front matter is not complete
        throw new IOException("YAML front matter is incomplete");
      }

      r.reset();
      readYaml(r);

      // read
      scan(r.read());
      scan(r.read());
      scan(r.read());
      scan(r.read());

      readMarkdown(r);
    }
  }

  private void readMarkdown(BufferedReader r) throws IOException {
    markdown = r.lines().collect(Collectors.joining("\n"));
  }

  /**
   * Reads the YAML front matter without separator, using the previously determined length.
   * 
   * @param r The reader.
   * 
   * @throws IOException If any I/O error occurs.
   */
  private void readYaml(BufferedReader r) throws IOException {
    char[] content = new char[l - SEPARATOR_LENGTH];
    r.read(content);
    yaml = new String(content);
  }

  /**
   * Scans the input for the YAML separator "---\n", used to partition the Markdown from an existing
   * front matter.
   * 
   * @param character The next input character.
   */
  private void scan(int character) {
    l++;

    if (character == -1) {
      i = -1;
      return;
    }

    boolean result;
    if (i != SEPARATOR_LENGTH - 1) {
      // -
      result = character == 45;
    } else {
      // \n
      result = character == 10;
    }
    
    if (!result) {
      // reset
      i = 0;
    } else {
      // increase, so that i will become 4 (the length of the YAML separator)
      i++;
    }
  }
}
