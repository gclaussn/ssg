package com.github.gclaussn.ssg;

import java.util.List;
import java.util.Optional;

import com.github.gclaussn.ssg.data.PageData;

public interface SiteGenerator {

  /** Name of the template model, providing builtin functions. */
  static final String FUNCTIONS = "_fn";

  /** Name of the template model, providing generator extensions. */
  static final String EXTENSIONS = "_ext";

  PageData compilePageData(String pageId);

  List<SiteError> generate();

  Optional<SiteError> generatePage(String pageId);

  List<SiteError> generatePageSet(String pageSetId);

  SiteGeneratorFn getFunctions();
}
