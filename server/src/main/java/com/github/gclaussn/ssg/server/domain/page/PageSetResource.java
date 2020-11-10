package com.github.gclaussn.ssg.server.domain.page;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import com.github.gclaussn.ssg.PageSet;
import com.github.gclaussn.ssg.server.AbstractResource;

@Path("/page-sets")
@Produces(MediaType.APPLICATION_JSON)
public class PageSetResource extends AbstractResource {

  @Path("/{pageSetId : .+}")
  @GET
  public PageSetDTO get(@PathParam("pageSetId") String pageSetId) {
    if (!site.hasPageSet(pageSetId)) {
      throw new WebApplicationException(Status.NOT_FOUND);
    }

    PageSet pageSet = site.getPageSet(pageSetId);

    return PageSetDTO.of(pageSet);
  }

  @GET
  public List<PageSetDTO> getAll() {
    return site.getPageSets().stream().sorted().map(PageSetDTO::of).collect(Collectors.toList());
  }

  @Path("/{pageSetId : .+}/pages")
  @GET
  public List<PageDTO> getPages(@PathParam("pageSetId") String pageSetId) {
    if (!site.hasPageSet(pageSetId)) {
      throw new WebApplicationException(Status.NOT_FOUND);
    }

    PageSet pageSet = site.getPageSet(pageSetId);

    return pageSet.getPages().stream().map(PageDTO::of).collect(Collectors.toList());
  }
}
