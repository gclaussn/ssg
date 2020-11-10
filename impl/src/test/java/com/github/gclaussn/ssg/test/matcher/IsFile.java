package com.github.gclaussn.ssg.test.matcher;

import java.nio.file.Files;
import java.nio.file.Path;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class IsFile extends TypeSafeMatcher<Path> {

  @Override
  public void describeTo(Description description) {
    description.appendText("file");
  }

  @Override
  protected boolean matchesSafely(Path item) {
    return Files.isRegularFile(item);
  }
}
