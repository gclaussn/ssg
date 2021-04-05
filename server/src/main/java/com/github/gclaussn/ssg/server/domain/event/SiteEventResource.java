package com.github.gclaussn.ssg.server.domain.event;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.server.AbstractResource;

@Path("/site-events")
@Produces(MediaType.APPLICATION_JSON)
public class SiteEventResource extends AbstractResource {

  @GET
  public List<SiteEventDTO> get() {
    return map(site.getEventStore().getEvents());
  }

  @Path("/{sourceId : .+}")
  @GET
  public List<SiteEventDTO> get(
      @PathParam("sourceId") String sourceId,
      @QueryParam("from") @DefaultValue("-1") long from
  ) {
    if (from >= 0L) {
      return map(site.getEventStore().getEvents(sourceId, from));
    } else {
      return map(site.getEventStore().getEvents(sourceId));
    }
  }

  protected List<SiteEventDTO> map(List<SiteEvent> events) {
    return events.stream().map(SiteEventDTO::of).collect(Collectors.toList());
  }
}
