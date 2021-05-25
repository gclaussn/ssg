package com.github.gclaussn.ssg;

import java.io.IOException;
import java.nio.file.Path;

import de.neuland.jade4j.exceptions.JadeException;

public interface SiteErrorBuilder {

  SiteError errorBeanExecutionFailed(Exception e, String beanId);

  SiteError errorBeanNotInitialized(Exception e, String beanId);

  SiteError errorModelNotRead(IOException e);

  SiteError errorModelNotWritten(IOException e, Path modelPath);

  SiteError errorOutputDirectoryNotCreated(IOException e);

  SiteError errorOutputDirectoryNotDeleted(IOException e);

  SiteError errorPageNotGenerated(IOException e);

  SiteError errorPageNotGenerated(JadeException e, Path templatePath);

  SiteError errorPageOutputDirectoryNotCreated(IOException e);

  SiteError errorPageSourceDirectoryNotCreated(IOException e);

  SiteError errorPageSetNotTraversed(IOException e);

  SiteError errorSiteFileServeFailed(IOException e);

  SiteErrorBuilder source(Source source);

  SiteErrorBuilder source(SourceType sourceType, String sourceId);
}
