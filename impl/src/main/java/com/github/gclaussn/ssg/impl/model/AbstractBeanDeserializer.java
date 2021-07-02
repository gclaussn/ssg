package com.github.gclaussn.ssg.impl.model;

import static java.lang.String.format;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.gclaussn.ssg.PageFilter;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.conf.TypeLookup;

/**
 * Generic deserializer for beans.<br>
 * Each bean definition node must contain these fields: "class": Class name (full or simple, if it
 * is not ambiguous) of the implementation type and "model": Object node, used deserialize the
 * implementation type.
 *
 * @param <T> The bean implementation type (e.g. the {@link PageFilterBeanImpl}).
 * 
 * @param <I> The bean type (e.g. {@link PageFilter}).
 */
abstract class AbstractBeanDeserializer<T, I> extends JsonDeserializer<T> {

  protected static final String FIELD_CLASS = "class";
  protected static final String FIELD_MODEL = "model";

  protected final Site site;

  /** Data structure, used to lookup types by full or simple class name. */
  private final TypeLookup<I> typeLookup;

  AbstractBeanDeserializer(Site site, TypeLookup<I> typeLookup) {
    this.site = site;
    this.typeLookup = typeLookup;
  }

  @Override
  public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    String id = ctxt.getParser().getCurrentName();

    ObjectCodec oc = p.getCodec();

    JsonNode jsonNode = oc.readTree(p);

    String implClassName = getImplClassName(jsonNode);
    Class<? extends I> implClass = lookup(implClassName);

    JsonNode modelNode = jsonNode.get(FIELD_MODEL);
    if (modelNode != null && !modelNode.isObject()) {
      throw new RuntimeException(format("Field '%s' must be an object or null", FIELD_MODEL));
    }

    I impl;
    if (modelNode != null) {
      impl = oc.treeToValue(modelNode, implClass);
    } else {
      impl = createImpl(implClass);
    }

    return createBean(id, impl);
  }

  /**
   * Creates a new bean based on the previously created implementation.
   * 
   * @param id The bean's ID.
   * 
   * @param impl The underlying implementation.
   * 
   * @return The newly created bean.
   */
  protected abstract T createBean(String id, I impl);

  protected I createImpl(Class<? extends I> implClass) {
    try {
      return implClass.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new RuntimeException(format("Class '%s' could not be instantiated", implClass), e);
    }
  }

  private RuntimeException fieldNotSet(String fieldName) {
    return new RuntimeException(format("Field '%s' field must be set", fieldName));
  }

  private RuntimeException fieldNotTextual(String fieldName) {
    return new RuntimeException(format("Field '%s' field must be textual", fieldName));
  }

  protected String getImplClassName(JsonNode jsonNode) {
    if (!jsonNode.has(FIELD_CLASS)) {
      throw fieldNotSet(FIELD_CLASS);
    }

    JsonNode implClassNode = jsonNode.get(FIELD_CLASS);
    if (!implClassNode.isTextual()) {
      throw fieldNotTextual(FIELD_CLASS);
    }

    return implClassNode.asText();
  }

  protected Class<? extends I> lookup(String implClassName) {
    Class<? extends I> type = typeLookup.lookup(implClassName);
    if (type == null) {
      throw new RuntimeException(format("Class '%s' not found", implClassName));
    }

    return type;
  }
}
