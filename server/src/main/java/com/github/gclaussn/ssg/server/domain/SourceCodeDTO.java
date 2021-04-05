package com.github.gclaussn.ssg.server.domain;

import com.github.gclaussn.ssg.file.SiteFileType;

public class SourceCodeDTO {

  private String code;
  private SiteFileType fileType;
  private int from;
  private int to;

  public String getCode() {
    return code;
  }

  public SiteFileType getFileType() {
    return fileType;
  }

  public int getFrom() {
    return from;
  }

  public int getTo() {
    return to;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void setFileType(SiteFileType fileType) {
    this.fileType = fileType;
  }

  public void setFrom(int from) {
    this.from = from;
  }

  public void setTo(int to) {
    this.to = to;
  }
}
