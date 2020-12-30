package com.github.gclaussn.ssg.impl;

import static java.lang.String.format;

import java.io.IOException;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.gclaussn.ssg.PageFilter;

/**
 * Generic deserializer for beans.<br />
 * Each YAML object node must contain these three fields:
 * <ul>
 *   <li>"id": ID of the bean</li>
 *   <li>"class": Class name of implementation type (fully or simple, if it is a default implementation)</li>
 *   <li>"model": Object node, used deserialize the implementation type</li>
 * </ul>
 *
 * @param <T> The bean type (e.g. the {@link PageFilterBeanImpl}).
 * 
 * @param <I> The implementation type (e.g. an implementation of {@link PageFilter}).
 */
abstract class AbstractBeanDeserializer<T, I> extends JsonDeserializer<T> {

  protected static final String FIELD_ID = "id";
  protected static final String FIELD_CLASS = "class";
  protected static final String FIELD_MODEL = "model";

  private final TypeLookup<I> typeLookup;

  AbstractBeanDeserializer(Set<Class<? extends I>> registeredTypes) {
    typeLookup = new TypeLookup<>(registeredTypes);
  }

  @Override
  public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    ObjectCodec oc = p.getCodec();

    JsonNode jsonNode = oc.readTree(p);

    String id = getId(jsonNode);
    String implClassName = getImplClassName(jsonNode);

    Class<? extends I> implClass = getImplClass(implClassName);

    JsonNode modelNode = jsonNode.get(FIELD_MODEL);
    if (modelNode != null && !modelNode.isObject()) {
      throw new RuntimeException(format("Field '%s' must be an object, if provided", FIELD_MODEL));
    }

    I implementation;
    if (modelNode != null) {
      implementation = oc.treeToValue(modelNode, implClass);
    } else {
      implementation = createImplementation(implClass);
    }

    return createBean(id, implementation);
  }

  /**
   * Creates a new bean based on the previously created implementation.
   * 
   * @param id The bean's ID.
   * 
   * @param implementation The underlying implementation.
   * 
   * @return The newly created bean.
   */
  protected abstract T createBean(String id, I implementation);

  protected I createImplementation(Class<? extends I> implClass) {
    try {
      return implClass.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new RuntimeException(format("Class '%s' could not be instantiated", implClass), e);
    }
  }

  private RuntimeException fieldNotProvided(String fieldName) {
    return new RuntimeException(format("Field '%s' field must be provided", fieldName));
  }

  private RuntimeException fieldNotTextual(String fieldName) {
    return new RuntimeException(format("Field '%s' field must be textual", fieldName));
  }

  protected String getId(JsonNode jsonNode) {
    if (!jsonNode.has(FIELD_ID)) {
      throw fieldNotProvided(FIELD_ID);
    }

    JsonNode idNode = jsonNode.get(FIELD_ID);
    if (!idNode.isTextual()) {
      throw fieldNotTextual(FIELD_ID);
    }

    return idNode.asText();
  }

  protected Class<? extends I> getImplClass(String implClassName) {
    Class<? extends I> type = typeLookup.lookup(implClassName);
    if (type == null) {
      throw new RuntimeException(format("Class '%s' not found", implClassName));
    }

    return type;
  }

  protected String getImplClassName(JsonNode jsonNode) {
    if (!jsonNode.has(FIELD_CLASS)) {
      throw fieldNotProvided(FIELD_CLASS);
    }

    JsonNode implClassNode = jsonNode.get(FIELD_CLASS);
    if (!implClassNode.isTextual()) {
      throw fieldNotTextual(FIELD_CLASS);
    }

    return implClassNode.asText();
  }
}
