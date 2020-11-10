package com.github.gclaussn.ssg;

public interface Source extends Comparable<Source> {

  @Override
  default int compareTo(Source source) {
    return getId().compareTo(source.getId());
  }

  String getId();

  SourceType getType();
}
