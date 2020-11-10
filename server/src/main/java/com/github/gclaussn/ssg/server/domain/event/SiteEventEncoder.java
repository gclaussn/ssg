package com.github.gclaussn.ssg.server.domain.event;

import java.io.IOException;
import java.io.Writer;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gclaussn.ssg.event.SiteEvent;
import com.github.gclaussn.ssg.server.provider.CustomJsonProvider;

public class SiteEventEncoder implements Encoder.TextStream<SiteEvent> {

  private ObjectMapper objectMapper;

  @Override
  public void init(EndpointConfig config) {
    objectMapper = CustomJsonProvider.createMapper();
  }

  @Override
  public void encode(SiteEvent event, Writer writer) throws EncodeException, IOException {
    objectMapper.writeValue(writer, SiteEventDTO.of(event));
  }

  @Override
  public void destroy() {
    // nothing to do here
  }
}
