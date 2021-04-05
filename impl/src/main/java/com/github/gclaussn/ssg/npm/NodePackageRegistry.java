package com.github.gclaussn.ssg.npm;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

/**
 * Proxy interface for the NPM registry API.
 */
@Consumes(MediaType.APPLICATION_JSON)
public interface NodePackageRegistry {

  @Path("/{packageName}/-/{packageName}-{version}.tgz")
  @GET
  InputStream download(@PathParam("packageName") String packageName, @PathParam("version") String version);

  @Path("/{packageName}/{version}")
  @GET
  NodePackageInfo getPackage(@PathParam("packageName") String packageName, @PathParam("version") String version);
}
