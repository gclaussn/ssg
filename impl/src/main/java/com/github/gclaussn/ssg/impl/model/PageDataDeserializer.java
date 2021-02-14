package com.github.gclaussn.ssg.impl.model;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.gclaussn.ssg.data.PageData;

class PageDataDeserializer extends JsonDeserializer<PageData> {

  @Override
  @SuppressWarnings("unchecked")
  public PageData deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    Map<String, Object> data = (Map<String, Object>) p.readValueAs(Map.class);

    return PageData.of(data);
  }
}
