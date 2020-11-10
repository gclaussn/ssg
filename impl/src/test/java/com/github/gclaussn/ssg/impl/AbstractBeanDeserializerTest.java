package com.github.gclaussn.ssg.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.gclaussn.ssg.builtin.selector.PageSetSelector;
import com.github.gclaussn.ssg.data.PageDataSelector;

public class AbstractBeanDeserializerTest {

  private SiteConfImpl conf;

  @Before
  public void setUp() {
    conf = new SiteConfImpl();
    conf.pageDataSelectorTypes = Collections.singleton(PageSetSelector.class);
  }

  @Test
  public void testGetId() {
    ObjectNode jsonNode = JsonNodeFactory.instance.objectNode();

    try {
      new PageDataSelectorDeserializer(conf).getId(jsonNode);
      fail("should throw RuntimeException");
    } catch (RuntimeException e) {
      assertThat(e.getMessage(), notNullValue());
    }

    jsonNode.put(AbstractBeanDeserializer.FIELD_ID, Boolean.TRUE);

    try {
      new PageDataSelectorDeserializer(conf).getId(jsonNode);
      fail("should throw RuntimeException");
    } catch (RuntimeException e) {
      assertThat(e.getMessage(), notNullValue());
    }

    jsonNode.put(AbstractBeanDeserializer.FIELD_ID, "beanA");

    String id = new PageDataSelectorDeserializer(conf).getId(jsonNode);
    assertThat(id, equalTo(jsonNode.get(AbstractBeanDeserializer.FIELD_ID).asText()));
  }

  @Test
  public void testGetImplClassName() {
    ObjectNode jsonNode = JsonNodeFactory.instance.objectNode();

    try {
      new PageDataSelectorDeserializer(conf).getImplClassName(jsonNode);
      fail("should throw RuntimeException");
    } catch (RuntimeException e) {
      assertThat(e.getMessage(), notNullValue());
    }

    jsonNode.put(AbstractBeanDeserializer.FIELD_CLASS, Boolean.TRUE);

    try {
      new PageDataSelectorDeserializer(conf).getImplClassName(jsonNode);
      fail("should throw RuntimeException");
    } catch (RuntimeException e) {
      assertThat(e.getMessage(), notNullValue());
    }

    jsonNode.put(AbstractBeanDeserializer.FIELD_CLASS, "beanA");

    String id = new PageDataSelectorDeserializer(conf).getImplClassName(jsonNode);
    assertThat(id, equalTo(jsonNode.get(AbstractBeanDeserializer.FIELD_CLASS).asText()));
  }

  @Test
  public void testGetImplClass() {
    new PageDataSelectorDeserializer(conf).getImplClass(PageSetSelector.class.getName());
  }

  public void testGetImplClassNotFound() {
    new PageDataSelectorDeserializer(conf).getImplClass("org.example.Impl");
  }

  @Test
  public void testCreateImplementation() {
    PageDataSelector pageDataSelector = new PageDataSelectorDeserializer(conf)
        .createImplementation(PageSetSelector.class);
    
    assertThat(pageDataSelector, notNullValue());
  }

  /**
   * Interface (as test class) cannot be instantiated.
   */
  @Test(expected = RuntimeException.class)
  public void testCreateImplementationNotInstantiated() {
    new PageDataSelectorDeserializer(conf).createImplementation(PageDataSelector.class);
  }
}
