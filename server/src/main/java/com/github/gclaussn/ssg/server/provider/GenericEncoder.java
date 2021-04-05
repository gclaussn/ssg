package com.github.gclaussn.ssg.server.provider;

import java.io.IOException;
import java.io.Writer;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GenericEncoder implements Encoder.TextStream<Object> {

  private ObjectMapper objectMapper;

  @Override
  public void init(EndpointConfig config) {
    objectMapper = CustomJsonProvider.createMapper();
  }

  @Override
  public void encode(Object data, Writer writer) throws EncodeException, IOException {
    objectMapper.writeValue(writer, data);
  }

  @Override
  public void destroy() {
    // nothing to do here
  }
}
