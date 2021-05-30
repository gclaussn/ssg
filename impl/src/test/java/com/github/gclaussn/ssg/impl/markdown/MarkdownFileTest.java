package com.github.gclaussn.ssg.impl.markdown;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import com.github.gclaussn.ssg.impl.model.SiteModelRepositoryTest;

public class MarkdownFileTest {

  private MarkdownFile support;

  @Before
  public void setUp() {
    support = new MarkdownFile(null);
  }

  @Test
  public void testRead() throws IOException {
    support.read(toInputStream("---\nx: y\n---\nsome text"));
    assertThat(support.hasYaml(), is(true));
    assertThat(support.getYaml(), equalTo("x: y\n"));
    assertThat(support.getMarkdown(), equalTo("some text"));
  }

  @Test
  public void testReadFrontMatterEmpty() throws IOException {
    support.read(toInputStream("---\n\n---\nsome text"));
    assertThat(support.hasYaml(), is(false));
    assertThat(support.getYaml(), equalTo("\n"));
    assertThat(support.getMarkdown(), equalTo("some text"));
  }

  @Test(expected = IOException.class)
  public void testReadFrontMatterNotComplete() throws IOException {
    support.read(toInputStream("---\nx: y\n"));
  }

  /**
   * Not correct: Line break after "---" is missing.
   */
  @Test(expected = IOException.class)
  public void testReadFrontMatterNotCorrect() throws IOException {
    support.read(toInputStream("---\nx: y\n---Test 123"));
  }

  @Test
  public void testReadFrontMatterNotExisting() throws IOException {
    support.read(toInputStream("--some text"));
    assertThat(support.hasYaml(), is(false));
    assertThat(support.getYaml(), nullValue());
    assertThat(support.getMarkdown(), equalTo("--some text"));
  }

  @Test
  public void testReadMarkdownEmpty() throws IOException {
    support.read(toInputStream(""));
    assertThat(support.hasYaml(), is(false));
    assertThat(support.getYaml(), nullValue());
    assertThat(support.getMarkdown(), equalTo(""));
  }

  @Test
  public void testFrom() throws IOException {
    String testResource = SiteModelRepositoryTest.class.getName().replace('.', '/');

    Path filePath = Paths.get("./src/test/resources/" + testResource + "/src/page-with-markdown-front-matter.md");

    MarkdownFile markdownFile = MarkdownFile.from(filePath);
    assertThat(markdownFile, notNullValue());
    assertThat(markdownFile.getMarkdown(), containsString("Test 123"));
    assertThat(markdownFile.hasYaml(), is(true));
    assertThat(markdownFile.getYaml(), equalTo("data:\n  x: y\n"));
  }

  protected InputStream toInputStream(String data) {
    return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
  }
}
