package com.github.gclaussn.ssg.server.domain;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.github.gclaussn.ssg.server.AbstractResource;

@Path("/site")
@Produces(MediaType.APPLICATION_JSON)
public class SiteResource extends AbstractResource {

  @GET
  public SiteDTO get() {
    return SiteDTO.of(site);
  }
}
