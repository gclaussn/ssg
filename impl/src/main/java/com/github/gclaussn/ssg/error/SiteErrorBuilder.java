package com.github.gclaussn.ssg.error;

import java.io.IOException;

import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.SourceType;

import de.neuland.jade4j.exceptions.JadeException;

public interface SiteErrorBuilder {

  SiteError errorBeanExecutionFailed(Exception e, String beanId);

  SiteError errorBeanNotInitialized(Exception e, String beanId);

  SiteError errorModelNotRead(IOException e);

  SiteError errorOutputDirectoryNotCreated(IOException e);

  SiteError errorOutputDirectoryNotDeleted(IOException e);

  SiteError errorPageNotGenerated(IOException e);

  SiteError errorPageNotGenerated(JadeException e);

  SiteError errorPageOutputDirectoryNotCreated(IOException e);

  SiteError errorPageSetNotTraversed(IOException e);

  SiteError errorSiteFileServeFailed(IOException e);

  SiteErrorBuilder source(Source source);

  SiteErrorBuilder source(SourceType sourceType, String sourceId);
}
