package com.github.gclaussn.ssg.server.domain.plugin;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.github.gclaussn.ssg.plugin.SitePluginActionDesc;
import com.github.gclaussn.ssg.plugin.SitePluginException;
import com.github.gclaussn.ssg.server.AbstractResource;
import com.github.gclaussn.ssg.server.ServerDTO;
import com.github.gclaussn.ssg.server.domain.plugin.action.SitePluginActionTaskEndpoint;

@Path("/site-plugins")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SitePluginResource extends AbstractResource {

  private final SitePluginActionTaskEndpoint taskEndpoint;

  public SitePluginResource(SitePluginActionTaskEndpoint taskEndpoint) {
    this.taskEndpoint = taskEndpoint;
  }

  @Path("/actions/{typeName : .+}/execute")
  @POST
  @Produces(MediaType.TEXT_PLAIN)
  public Response executePluginAction(
      @PathParam("typeName") String typeName,
      Map<String, Object> properties,
      @Context HttpHeaders httpHeaders
  ) {
    ServerDTO server = getServer(httpHeaders);

    String taskId = taskEndpoint.submit(site, toPluginAction(typeName), properties);
    String taskUrl = String.format("%s/tasks?id=%s", server.getWebSocketUrl(), taskId);

    return Response.accepted(taskUrl).type(MediaType.TEXT_PLAIN).build();
  }
  
  @GET
  public List<SitePluginDTO> getAll() {
    return site.getPluginManager().getPlugins().stream().map(SitePluginDTO::of).collect(Collectors.toList());
  }

  @Path("/actions/{typeName : .+}")
  @GET
  public SitePluginActionDesc getPluginAction(@PathParam("typeName") String typeName) {
    try {
      return site.getPluginManager().getPluginAction(toPluginAction(typeName));
    } catch (SitePluginException e) {
      throw new WebApplicationException(Status.NOT_FOUND);
    }
  }

  protected String toPluginAction(String typeName) {
    return typeName.replace('/', '.');
  }
}
