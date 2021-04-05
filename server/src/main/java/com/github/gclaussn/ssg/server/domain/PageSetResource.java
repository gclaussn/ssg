package com.github.gclaussn.ssg.server.domain;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.github.gclaussn.ssg.server.AbstractResource;

@Path("/page-sets")
@Produces(MediaType.APPLICATION_JSON)
public class PageSetResource extends AbstractResource {

  @GET
  public List<PageSetDTO> get() {
    return site.getPageSets().stream().sorted().map(PageSetDTO::of).collect(Collectors.toList());
  }

  @Path("/{pageSetId : .+}")
  @GET
  public PageSetDTO getById(@PathParam("pageSetId") String pageSetId) {
    checkHasPageSet(pageSetId);

    return PageSetDTO.of(site.getPageSet(pageSetId));
  }

  @Path("/{pageSetId : .+}/pages")
  @GET
  public List<PageDTO> getPages(@PathParam("pageSetId") String pageSetId) {
    checkHasPageSet(pageSetId);

    return site.getPageSet(pageSetId).getPages().stream().map(PageDTO::of).collect(Collectors.toList());
  }
}
