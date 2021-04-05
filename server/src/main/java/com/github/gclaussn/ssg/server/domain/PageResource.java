package com.github.gclaussn.ssg.server.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.PageInclude;
import com.github.gclaussn.ssg.server.AbstractResource;

@Path("/pages")
@Produces(MediaType.APPLICATION_JSON)
public class PageResource extends AbstractResource {

  @GET
  public List<PageDTO> get() {
    return site.getPages().stream().sorted().map(PageDTO::of).collect(Collectors.toList());
  }

  @Path("/{pageId : .+}")
  @GET
  public PageDTO getById(@PathParam("pageId") String pageId) {
    checkHasPage(pageId);

    return PageDTO.of(site.getPage(pageId));
  }

  @Path("/{pageId : .+}/data")
  @GET
  public Map<String, Object> getData(@PathParam("pageId") String pageId) {
    checkHasPage(pageId);

    return site.getGenerator().compilePageData(pageId).getRootMap();
  }

  @Path("/{pageId : .+}/includes")
  @GET
  public List<PageIncludeDTO> getIncludes(
      @PathParam("pageId") String pageId,
      @QueryParam("recursive") @DefaultValue("false") boolean recursive
  ) {
    checkHasPage(pageId);

    Page page = site.getPage(pageId);

    Set<PageInclude> pageIncludes;
    if (recursive) {
      pageIncludes = getIncludesRecursive(page);
    } else {
      pageIncludes = page.getPageIncludes();
    }

    return pageIncludes.stream().map(PageIncludeDTO::of).collect(Collectors.toList());
  }

  protected Set<PageInclude> getIncludesRecursive(Page page) {
    Set<PageInclude> pageIncludes = new TreeSet<>();

    Queue<PageInclude> queue = new LinkedList<>(page.getPageIncludes());
    while (!queue.isEmpty()) {
      PageInclude pageInclude = queue.poll();

      if (pageIncludes.contains(pageInclude)) {
        continue;
      }

      pageIncludes.add(pageInclude);

      queue.addAll(pageInclude.getPageIncludes());
    }

    return pageIncludes;
  }
}
