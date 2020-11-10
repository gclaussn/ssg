package com.github.gclaussn.ssg.server.domain.event;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import com.github.gclaussn.ssg.EventDrivenSite;
import com.github.gclaussn.ssg.EventStore;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.server.AbstractResource;

@Path("/site-events")
@Produces(MediaType.APPLICATION_JSON)
public class SiteEventResource extends AbstractResource {

  private EventStore eventStore;

  @Override
  protected void init(Site site) {
    super.init(site);

    try {
      eventStore = ((EventDrivenSite) site).getEventStore();
    } catch (ClassCastException e) {
      eventStore = null;
    }
  }

  @GET
  public List<SiteEventDTO> get() {
    return map(eventStore.getEvents());
  }

  @Path("/{sourceId : .+}")
  @GET
  public List<SiteEventDTO> get(
      @PathParam("sourceId") String sourceId,
      @QueryParam("from") @DefaultValue("-1") long from
  ) {
    if (eventStore == null) {
      throw new WebApplicationException(Status.METHOD_NOT_ALLOWED);
    }

    if (from >= 0L) {
      return map(eventStore.getEvents(sourceId, from));
    } else {
      return map(eventStore.getEvents(sourceId));
    }
  }

  protected List<SiteEventDTO> map(List<SiteEvent> events) {
    return events.stream().map(SiteEventDTO::of).collect(Collectors.toList());
  }
}
