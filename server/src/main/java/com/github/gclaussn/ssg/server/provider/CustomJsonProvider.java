package com.github.gclaussn.ssg.server.provider;

import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

public class CustomJsonProvider extends JacksonJsonProvider {

  public static ObjectMapper createMapper() {
    SimpleModule module = new SimpleModule();
    module.addSerializer(Path.class, new CustomPathSerializer());

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(module);

    return objectMapper;
  }

  public CustomJsonProvider() {
    super(createMapper());
  }
}
