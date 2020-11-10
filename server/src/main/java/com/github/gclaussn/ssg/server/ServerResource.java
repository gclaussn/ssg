package com.github.gclaussn.ssg.server;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.github.gclaussn.ssg.file.SiteFileWatcher;

@Path("/server")
@Produces(MediaType.APPLICATION_JSON)
public class ServerResource extends AbstractResource {

  private final SiteFileWatcher siteFileWatcher;

  public ServerResource(SiteFileWatcher siteFileWatcher) {
    this.siteFileWatcher = siteFileWatcher;
  }

  @GET
  public ServerDTO get(@Context HttpHeaders httpHeaders) {
    String headerValue = httpHeaders.getHeaderString(HttpHeaders.HOST);
    if (headerValue == null) {
      throw new WebApplicationException(Status.BAD_REQUEST);
    }

    int index = headerValue.indexOf(':');

    ServerDTO server = new ServerDTO();
    server.setHost(headerValue.substring(0, index));
    server.setPort(Integer.parseInt(headerValue.substring(index + 1)));

    return server;
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

      synchronized (siteFileWatcher) {
        // stop file watcher thread
        siteFileWatcher.stop();

        // notify waiting main thread
        siteFileWatcher.notify();
      }
    }, "ssg-server-stop").start();

    return Response.status(Status.ACCEPTED).build();
  }
}
