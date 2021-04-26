package com.github.gclaussn.ssg.impl;

import static com.github.gclaussn.ssg.file.SiteFileType.MD;
import static com.github.gclaussn.ssg.file.SiteFileType.YAML;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.SiteErrorBuilder;
import com.github.gclaussn.ssg.SiteErrorType;
import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.SourceType;
import com.github.gclaussn.ssg.impl.model.SourceImpl;

import de.neuland.jade4j.exceptions.JadeException;

/**
 * Builder, which provides methods to create common {@link SiteError}s.
 */
public class SiteErrorBuilderImpl implements SiteErrorBuilder {

  private static final ResourceBundle RESOURCE_BUNDLE;

  static {
    RESOURCE_BUNDLE = ResourceBundle.getBundle(SiteErrorBuilderImpl.class.getName());
  }

  private final Site site;

  private SiteErrorImpl error;

  public SiteErrorBuilderImpl(Site site) {
    this.site = site;

    error = new SiteErrorImpl();
  }

  @Override
  public SiteError errorBeanExecutionFailed(Exception e, String beanId) {
    error.cause = e;
    error.message = format(getModelName(), error.source.getId(), beanId);
    error.type = SiteErrorType.BEAN;

    return error;
  }

  @Override
  public SiteError errorBeanNotInitialized(Exception e, String beanId) {
    error.cause = e;
    error.message = format(beanId);
    error.type = SiteErrorType.BEAN;

    return error;
  }

  @Override
  public SiteError errorModelNotRead(IOException e) {
    SiteErrorLocationImpl location = new SiteErrorLocationImpl();
    location.path = getModelPath();

    error.cause = e;
    error.message = format(getModelName(), location.path);
    error.location = location;

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
    if (jsonLocation != null && MD.isPresent(location.path)) {
      // due to Markdown front matter separator
      location.line += 1;
    }

    return error;
  }

  @Override
  public SiteError errorOutputDirectoryNotCreated(IOException e) {
    error.cause = e;
    error.message = format(site.getOutputPath());
    error.type = SiteErrorType.IO;

    return error;
  }

  @Override
  public SiteError errorOutputDirectoryNotDeleted(IOException e) {
    error.cause = e;
    error.message = format(site.getOutputPath());
    error.type = SiteErrorType.IO;

    return error;
  }

  @Override
  public SiteError errorPageNotGenerated(IOException e) {
    error.cause = e;
    error.message = format(error.source.getId());
    error.type = SiteErrorType.IO;

    return error;
  }

  @Override
  public SiteError errorPageNotGenerated(JadeException e) {
    SiteErrorLocationImpl location = new SiteErrorLocationImpl();
    location.path = Paths.get(e.getFilename());
    location.line = e.getLineNumber();

    error.cause = e;
    error.location = location;
    error.message = format(error.source.getId());
    error.type = SiteErrorType.TEMPLATE;

    return error;
  }

  @Override
  public SiteError errorPageOutputDirectoryNotCreated(IOException e) {
    error.cause = e;
    error.message = format(error.source.getId());
    error.type = SiteErrorType.IO;

    return error;
  }

  @Override
  public SiteError errorPageSetNotTraversed(IOException e) {
    error.cause = e;
    error.message = format(error.source.getId());
    error.type = SiteErrorType.IO;

    return error;
  }

  @Override
  public SiteError errorSiteFileServeFailed(IOException e) {
    error.cause = e;
    error.message = format();
    error.type = SiteErrorType.IO;

    return error;
  }

  @Override
  public SiteErrorBuilder source(Source source) {
    error.source = source;
    return this;
  }

  @Override
  public SiteErrorBuilder source(SourceType sourceType, String sourceId) {
    error.source = new SourceImpl(sourceType, sourceId);
    return this;
  }

  protected String format(Object... args) {
    return MessageFormat.format(RESOURCE_BUNDLE.getString(getMethodName()), args);
  }

  protected String getMethodName() {
    return StackWalker.getInstance().walk(stack -> stack.skip(2).findFirst()).get().getMethodName();
  }

  protected String getModelName() {
    if (error.source.getType() == null) {
      return "Site";
    }

    switch (error.source.getType()) {
      case PAGE:
        return "Page";
      case PAGE_INCLUDE:
        return "Page include";
      case PAGE_SET:
        return "Page set";
      default:
        return "Unknown";
    }
  }

  protected Path getModelPath() {
    if (error.source.getType() == null) {
      return site.getPath().resolve(Site.MODEL_NAME);
    }

    Path yamlPath = site.getSourcePath().resolve(YAML.appendTo(error.source.getId()));
    if (Files.exists(yamlPath)) {
      return yamlPath;
    } else {
      return site.getSourcePath().resolve(MD.appendTo(error.source.getId()));
    }
  }
}
