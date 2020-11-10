package com.github.gclaussn.ssg.server.provider;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class CustomExceptionMapper implements ExceptionMapper<Throwable> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CustomExceptionMapper.class);

  @Override
  public Response toResponse(Throwable t) {
    if (t instanceof WebApplicationException) {
      return ((WebApplicationException) t).getResponse();
    }

    LOGGER.error("Internal server error occurred", t);
    return Response.serverError().build();
  }
}
