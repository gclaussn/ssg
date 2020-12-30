package com.github.gclaussn.ssg.conf;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.lang3.StringUtils;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SiteProperty {

  /**
   * Provides an optional default value, which must not be blank.
   * 
   * @return The default value, if no property value and no environment variable has been specified.
   */
  String defaultValue() default StringUtils.EMPTY;

  /**
   * Determines if the property is documented in form of a {@link SitePropertyDesc}.
   * 
   * @return {@code true}, if the property is documented. Otherwise {@code false}.
   */
  boolean documented() default true;

  /**
   * Determines if the property is masked. If so, the related value or environment variable will be
   * printed as "***".
   * 
   * @return {@code true}, if the property is masked. Otherwise {@code false}.
   */
  boolean masked() default false;

  /**
   * Provides the property name, which must not be blank.
   * 
   * @return The name of the property.
   */
  String name();

  /**
   * Determines if the property is required. If so and there is no default value specified, a
   * {@link RuntimeException} is thrown during injection.
   * 
   * @return {@code true}, if the property is required. Otherwise {@code false}.
   */
  boolean required() default true;
}
