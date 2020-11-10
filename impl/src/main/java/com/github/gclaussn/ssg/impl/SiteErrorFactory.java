package com.github.gclaussn.ssg.impl;

import static com.github.gclaussn.ssg.file.SiteFileType.YAML;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.SiteErrorType;
import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.SourceType;

import de.neuland.jade4j.exceptions.JadeException;

/**
 * Internal factory class, which provides methods to create common {@link SiteError}s.
 */
class SiteErrorFactory {

  private final Site site;

  private final ResourceBundle resourceBundle;

  SiteErrorFactory(Site site) {
    this.site = site;

    resourceBundle = ResourceBundle.getBundle(this.getClass().getName());
  }

  public SiteError beanExecutionFailed(Exception e, String beanId, Source source) {
    SiteErrorImpl error = new SiteErrorImpl(e);
    error.message = format(getModelName(source), source.getId(), beanId);
    error.source = source;
    error.type = SiteErrorType.BEAN;

    return error;
  }

  public SiteError beanNotInitialized(Exception e, String beanId, Source source) {
    SiteErrorImpl error = new SiteErrorImpl(e);
    error.message = format(beanId);
    error.source = source;
    error.type = SiteErrorType.BEAN;

    return error;
  }

  public SiteError modelNotRead(IOException e, Source source) {
    Path modelPath = getModelPath(source);

    SiteErrorLocationImpl location = new SiteErrorLocationImpl(modelPath);

    SiteErrorImpl error = new SiteErrorImpl(e);
    error.message = format(getModelName(source), modelPath);
    error.location = location;
    error.source = source;
    
    JsonLocation jsonLocation = null;
    if (e instanceof JsonProcessingException) {
      jsonLocation = ((JsonProcessingException) e).getLocation();
      
      error.type = SiteErrorType.MODEL;
    } else {
      error.type = SiteErrorType.IO;
    }

    if (jsonLocation != null) {
      location.line = jsonLocation.getLineNr();
      location.column = jsonLocation.getColumnNr();
    }

    return error;
  }

  public SiteError outputDirectoryNotCreated(IOException e) {
    SiteErrorImpl error = new SiteErrorImpl(e);
    error.message = format(site.getOutputPath());
    error.type = SiteErrorType.IO;

    return error;
  }

  public SiteError outputDirectoryNotDeleted(IOException e) {
    SiteErrorImpl error = new SiteErrorImpl(e);
    error.message = format(site.getOutputPath());
    error.type = SiteErrorType.IO;

    return error;
  }

  public SiteError pageNotGenerated(IOException e, Page page) {
    SiteErrorImpl error = new SiteErrorImpl(e);
    error.message = format(page.getId());
    error.source = page;
    error.type = SiteErrorType.IO;

    return error;
  }

  public SiteError pageNotGenerated(JadeException e, Page page) {
    SiteErrorLocationImpl location = new SiteErrorLocationImpl(Paths.get(e.getFilename()));
    location.line = e.getLineNumber();

    SiteErrorImpl error = new SiteErrorImpl(e);
    error.location = location;
    error.message = format(page.getId());
    error.source = page;
    error.type = SiteErrorType.TEMPLATE;

    return error;
  }

  public SiteError pageOutputDirectoryNotCreated(IOException e, Page page) {
    SiteErrorImpl error = new SiteErrorImpl(e);
    error.message = format(page.getId());
    error.source = page;
    error.type = SiteErrorType.IO;

    return error;
  }

  public SiteError pageSetNotTraversed(IOException e, Source source) {
    SiteErrorImpl error = new SiteErrorImpl(e);
    error.message = format(source.getId());
    error.source = source;
    error.type = SiteErrorType.IO;

    return error;
  }

  public SiteError siteFileServeFailed(IOException e) {
    SiteErrorImpl error = new SiteErrorImpl(e);
    error.message = format();
    error.type = SiteErrorType.IO;

    return error;
  }

  protected String format(Object... args) {
    return MessageFormat.format(resourceBundle.getString(getMethodName()), args);
  }

  protected String getMethodName() {
    return StackWalker.getInstance().walk(stack -> stack.skip(2).findFirst()).get().getMethodName();
  }

  protected String getModelName(Source source) {
    switch (source.getType()) {
      case PAGE:
        return "Page";
      case PAGE_INCLUDE:
        return "Page include";
      case PAGE_SET:
        return "Page set";
      case SITE:
        return "Site";
      case UNKNOWN:
        // not possible here
    }

    return null;
  }

  protected Path getModelPath(Source source) {
    if (source.getType() == SourceType.SITE) {
      return site.getPath().resolve(Site.MODEL_NAME);
    } else {
      return site.getSourcePath().resolve(YAML.appendTo(source.getId()));
    }
  }
}
