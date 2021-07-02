package com.github.gclaussn.ssg.conf;

/**
 * Description of a {@link SiteProperty} annotated field.
 */
public interface SitePropertyDesc extends Comparable<SitePropertyDesc> {

  /**
   * Returns the default value in a string representation.
   * 
   * @return The default value, if not empty or blank. Otherwise {@code null}.
   */
  String getDefaultValue();

  String getDocumentation();

  String getName();

  SitePropertyType getType();

  String getTypeName();

  /**
   * Returns the actual configured property value, if the value is set the string representation is
   * returned. Otherwise the value is {@code null}.
   * 
   * @return The property value or {@code null}.
   */
  String getValue();

  /**
   * Provides the value of the associated environment variable.
   * 
   * @return The environment variable value or {@code null}.
   */
  String getVariable();

  /**
   * Provides the name of the associated environment variable.
   * 
   * @return The environment variable name.
   */
  String getVariableName();

  boolean isMasked();

  /**
   * Determines if the property is required or not. If the property specifies a default value, it will
   * not be required.
   * 
   * @return {@code true}, if the property is required and therefore must be provided. Otherwise
   *         {@code false}.
   */
  boolean isRequired();
}
