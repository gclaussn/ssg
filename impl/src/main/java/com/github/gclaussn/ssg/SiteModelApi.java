package com.github.gclaussn.ssg;

/**
 * API to create, modify and delete models ({@link Page}s, {@link PageSet}s, {@link PageInclude}s)
 * programmatically.
 */
public interface SiteModelApi {

  PageBuilder createPageBuilder(String pageId);
}
