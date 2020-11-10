package com.github.gclaussn.ssg.impl;

import java.nio.file.Path;

import com.github.gclaussn.ssg.SiteOutput;

class SiteOutputImpl implements SiteOutput {

  protected Path filePath;
  protected String name;
  protected String path;

  @Override
  public Path getFilePath() {
    return filePath;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getPath() {
    return path;
  }
}
