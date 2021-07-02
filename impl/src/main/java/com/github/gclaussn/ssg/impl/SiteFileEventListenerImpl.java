package com.github.gclaussn.ssg.impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Function;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.PageSet;
import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.SiteException;
import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.data.PageData;
import com.github.gclaussn.ssg.data.PageDataSelector;
import com.github.gclaussn.ssg.file.SiteFileEvent;
import com.github.gclaussn.ssg.file.SiteFileEventListener;
import com.github.gclaussn.ssg.file.SiteFileEventType;

class SiteFileEventListenerImpl implements SiteFileEventListener {

  private final SiteImpl site;

  private final Function<Page, PageData> pageDataCompiler;

  SiteFileEventListenerImpl(SiteImpl site) {
    this.site = site;

    // use special page data compiler that makes use of hash code values
    // to avoid unnecessary page generation
    pageDataCompiler = new HashCodeBasedPageDataCompiler();
  }

  /**
   * Handles the modification of source files.
   */
  @Override
  public void onEvent(SiteFileEvent event) {
    if (event.isPublic()) {
      // do not handle public files any further
      // since public files do not affect source loading or page generating
      return;
    }

    if (event.isSource()) {
      handleSource(event);
    } else {
      handleSite(event);
    }
  }

  /**
   * Generates the page, if the page data has changed. The {@link HashCodeBasedPageDataCompiler}
   * function is responsible for detecting changes. It the hash codes of {@link PageDataSelector}
   * results with previous invocations. If the page data has not changed, the compiler function
   * returns {@code null}.<br>
   * This method is only for dependent pages applicable.
   * 
   * @param page A dependent page to generate.
   * 
   * @return An error, if the compilation or the generation failed. Otherwise an empty optional.
   */
  protected Optional<SiteError> generateDependentPage(Page page) {
    // compile data
    PageData data;
    try {
      data = pageDataCompiler.apply(page);
    } catch (SiteException e) {
      return Optional.of(e.getError());
    }

    if (data == null) {
      // null indicates that the page data has not changed
      // therefore the generation is skipped
      return Optional.empty();
    }

    return site.generator.generatePage(page, data);
  }

  protected void handlePage(String pageId) {
    Optional<SiteError> error;
    Optional<PageSet> pageSet;

    // load
    pageSet = site.hasPage(pageId) ? site.getPage(pageId).getPageSet() : Optional.empty();
    if (pageSet.isPresent()) {
      error = site.repository.loadPage(pageId, pageSet.get().getId());
    } else {
      error = site.repository.loadPage(pageId);
    }
    if (error.isPresent()) {
      return;
    }

    // get loaded page
    Page page = site.getPage(pageId);
    if (page.isRejected()) {
      return;
    }

    // generate
    if (!page.isSkipped()) {
      error = page.generate();
    }
    if (!page.isSkipped() && error.isPresent()) {
      return;
    }

    pageSet = page.getPageSet();
    if (!pageSet.isPresent()) {
      // no other page is affected
      return;
    }

    String pageSetId = pageSet.get().getId();

    site.analyzePageSetUsage(pageSetId)
        .stream()
        // filter pages that should be generated
        .filter(Page::isGenerated)
        // use special method to generate dependent pages
        .forEach(this::generateDependentPage);
  }

  protected void handlePageInclude(String pageIncludeId) {
    Queue<String> queue = new LinkedList<>(Collections.singletonList(pageIncludeId));

    List<SiteError> errors = site.repository.loadPageIncludes(queue);
    if (!errors.isEmpty()) {
      return;
    }

    site.analyzePageIncludeUsage(pageIncludeId).stream().filter(Page::isGenerated).forEach(Page::generate);
  }

  protected void handlePageSet(String pageSetId) {
    List<SiteError> errors;

    // load
    errors = site.repository.loadPageSet(pageSetId);
    if (!errors.isEmpty()) {
      return;
    }

    PageSet pageSet = site.getPageSet(pageSetId);

    // generate
    if (!pageSet.isSkipped()) {
      errors = pageSet.generate();
    }
    if (!pageSet.isSkipped() && !errors.isEmpty()) {
      return;
    }

    site.analyzePageSetUsage(pageSetId).stream().filter(Page::isGenerated).forEach(Page::generate);
  }

  protected void handleSite(SiteFileEvent event) {
    if (event.getType() == SiteFileEventType.DELETE) {
      // ignore deletion of site.yaml, because the site cannot be closed
      // otherwise file events will not be handled anymore
      return;
    }

    site.load();
  }

  protected void handleSource(SiteFileEvent event) {
    // try to determine, what source (page, page set or page include) is affected
    Source source = site.repository.getSource(event.getPath());

    if (event.getType() == SiteFileEventType.DELETE) {
      site.repository.removeSource(source);
      return;
    }

    String sourceId = source.getId();

    switch (source.getType()) {
      case PAGE:
        handlePage(sourceId);
        break;
      case PAGE_INCLUDE:
        handlePageInclude(sourceId);
        break;
      case PAGE_SET:
        handlePageSet(sourceId);
        break;
      case UNKNOWN:
        // if page set is found, load and generate it instead
        Optional<String> pageSetId = site.repository.findPageSetId(sourceId);
        if (pageSetId.isPresent()) {
          handlePageSet(pageSetId.get());
        }
    }
  }
}
