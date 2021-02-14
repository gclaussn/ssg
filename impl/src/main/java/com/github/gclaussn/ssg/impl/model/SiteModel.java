package com.github.gclaussn.ssg.impl.model;

import java.util.LinkedHashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.gclaussn.ssg.Site;

/**
 * Site YAML model.
 * 
 * @see {@link Site#MODEL_NAME}
 */
class SiteModel {

  @JsonDeserialize(as = LinkedHashSet.class)
  protected Set<String> pages;
  @JsonDeserialize(as = LinkedHashSet.class)
  protected Set<String> pageSets;

  protected NodeModulesImpl nodeModules;
}
