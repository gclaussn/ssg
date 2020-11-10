package com.github.gclaussn.ssg.test;

import java.nio.file.Path;

import org.hamcrest.Matcher;

import com.github.gclaussn.ssg.test.matcher.IsDirectory;
import com.github.gclaussn.ssg.test.matcher.IsFile;

public class CustomMatcher {

  public static Matcher<Path> isDirectory() {
    return new IsDirectory();
  }

  public static Matcher<Path> isFile() {
    return new IsFile();
  }
}
