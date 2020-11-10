package com.github.gclaussn.ssg.server.provider;

import java.io.IOException;
import java.nio.file.Path;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CustomPathSerializer extends JsonSerializer<Path> {

  @Override
  public void serialize(Path value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    if (value != null) {
      gen.writeString(value.toAbsolutePath().normalize().toString());
    } else {
      gen.writeNull();
    }
  }
}
