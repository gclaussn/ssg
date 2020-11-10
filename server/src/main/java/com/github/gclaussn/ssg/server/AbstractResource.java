package com.github.gclaussn.ssg.server;

import com.github.gclaussn.ssg.Site;

public class AbstractResource {

  protected Site site;

  protected void init(Site site) {
    this.site = site;
  }
}
