package com.github.gclaussn.ssg.test.matcher;

import java.nio.file.Files;
import java.nio.file.Path;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class IsDirectory extends TypeSafeMatcher<Path> {

  @Override
  public void describeTo(Description description) {
    description.appendText("directory");
  }

  @Override
  protected boolean matchesSafely(Path item) {
    return Files.isDirectory(item);
  }
}
