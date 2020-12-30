package com.github.gclaussn.ssg.server;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.github.gclaussn.ssg.server.file.SiteFileWatcher;

@Path("/server")
@Produces(MediaType.APPLICATION_JSON)
public class ServerResource extends AbstractResource {

  private final SiteFileWatcher siteFileWatcher;
  private final Consumer<SiteFileWatcher> stopFunction;

  public ServerResource(SiteFileWatcher siteFileWatcher, Consumer<SiteFileWatcher> stopFunction) {
    this.siteFileWatcher = siteFileWatcher;
    this.stopFunction = stopFunction;
  }

  @GET
  public ServerDTO get(@Context HttpHeaders httpHeaders) {
    return getServer(httpHeaders);
  }

  @Path("/stop")
  @GET
  public Response stop() {
    new Thread(() -> {
      try {
        TimeUnit.MILLISECONDS.sleep(100L);
      } catch (InterruptedException e) {
        // ignore exception
      }

      stopFunction.accept(siteFileWatcher);
    }, "ssg-server-stop").start();

    return Response.status(Status.ACCEPTED).build();
  }
}
