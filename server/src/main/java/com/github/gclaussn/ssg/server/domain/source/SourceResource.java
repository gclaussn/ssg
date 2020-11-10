package com.github.gclaussn.ssg.server.domain.source;

import static com.github.gclaussn.ssg.file.SiteFileType.JADE;
import static com.github.gclaussn.ssg.file.SiteFileType.YAML;

import java.nio.file.Files;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.github.gclaussn.ssg.Source;
import com.github.gclaussn.ssg.SourceType;
import com.github.gclaussn.ssg.server.AbstractResource;

@Path("/sources")
@Produces(MediaType.APPLICATION_JSON)
public class SourceResource extends AbstractResource {

  protected java.nio.file.Path determineJadePath(String sourceId) {
    String id = sourceId;
    
    int index;
    do {
      java.nio.file.Path path = site.getSourcePath().resolve(JADE.appendTo(id));
      if (Files.isRegularFile(path)) {
        return path;
      }
      
      index = id.lastIndexOf('/');
      id = id.substring(0, index);
      
      Source source = site.getSource(id);
      if (source.getType() != SourceType.UNKNOWN && source.getType() != SourceType.PAGE_SET) {
        break;
      }
    } while (index != -1);

    return null;
  }

  @Path("/{sourceId : .+}/jade")
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public Response getJade(
      @PathParam("sourceId") String sourceId,
      @QueryParam("from") @DefaultValue("1") int from,
      @QueryParam("to") @DefaultValue("-1") int to
  ) {
    java.nio.file.Path path = determineJadePath(sourceId);
    if (path != null) {
      return Response.ok(new SourceCodeResponse(path, from, to)).build();
    } else {
      return Response.status(Status.NOT_FOUND).build();
    }
  }

  @Path("/{sourceId : .+}/yaml")
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public Response getYaml(
      @PathParam("sourceId") String sourceId,
      @QueryParam("from") @DefaultValue("1") int from,
      @QueryParam("to") @DefaultValue("-1") int to
  ) {
    java.nio.file.Path path = site.getSourcePath().resolve(YAML.appendTo(sourceId));

    if (Files.isRegularFile(path)) {
      return Response.ok(new SourceCodeResponse(path, from, to)).build();
    } else {
      return Response.status(Status.NOT_FOUND).build();
    }
  }
}
