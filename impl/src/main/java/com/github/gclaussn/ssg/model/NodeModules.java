package com.github.gclaussn.ssg.model;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

public interface NodeModules {

  List<String> getIncludes();

  Predicate<Path> getMatcher();
}
