package com.github.gclaussn.ssg.impl.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.builtin.selector.PageSetSelector;
import com.github.gclaussn.ssg.conf.SiteConf;
import com.github.gclaussn.ssg.data.PageDataSelector;
import com.github.gclaussn.ssg.impl.TypeLookupImpl;

public class AbstractBeanDeserializerTest {

  private PageDataSelectorDeserializer deserializer;

  private ObjectMapper objectMapper;

  @Before
  public void setUp() {
    Set<Class<? extends PageDataSelector>> types = Collections.singleton(PageSetSelector.class);

    SiteConf siteConf = Mockito.mock(SiteConf.class);
    when(siteConf.getPageDataSelectorTypes()).thenReturn(new TypeLookupImpl<>(types));

    Site site = Mockito.mock(Site.class);
    when(site.getConf()).thenReturn(siteConf);

    Collections.singleton(PageSetSelector.class);

    deserializer = new PageDataSelectorDeserializer(site);

    SimpleModule module = new SimpleModule();
    module.addDeserializer(PageDataSelectorBeanImpl.class, deserializer);

    objectMapper = new ObjectMapper();
    objectMapper.registerModule(module);
  }

  @Test
  public void testCreateImpl() {
    PageDataSelector pageDataSelector = deserializer.createImpl(PageSetSelector.class);
    assertThat(pageDataSelector, notNullValue());
  }

  /**
   * Interface (as test class) cannot be instantiated.
   */
  @Test(expected = RuntimeException.class)
  public void testCreateImplNotInstantiated() {
    deserializer.createImpl(PageDataSelector.class);
  }

  @Test
  public void testDeserialize() throws JsonProcessingException {
    ObjectNode jsonNode = JsonNodeFactory.instance.objectNode();
    jsonNode.put(AbstractBeanDeserializer.FIELD_ID, "test");
    jsonNode.put(AbstractBeanDeserializer.FIELD_CLASS, PageSetSelector.class.getSimpleName());
    jsonNode.set(AbstractBeanDeserializer.FIELD_MODEL, JsonNodeFactory.instance.objectNode());

    PageDataSelectorBeanImpl bean = objectMapper.treeToValue(jsonNode, PageDataSelectorBeanImpl.class);
    assertThat(bean, notNullValue());
    assertThat(bean.getId(), equalTo(jsonNode.get(AbstractBeanDeserializer.FIELD_ID).asText()));
  }

  @Test(expected = RuntimeException.class)
  public void testDeserializeClassNotFound() throws JsonProcessingException {
    ObjectNode jsonNode = JsonNodeFactory.instance.objectNode();
    jsonNode.put(AbstractBeanDeserializer.FIELD_ID, "test");
    jsonNode.put(AbstractBeanDeserializer.FIELD_CLASS, "org.example.Impl");
    jsonNode.put(AbstractBeanDeserializer.FIELD_MODEL, Boolean.TRUE);

    objectMapper.treeToValue(jsonNode, PageDataSelectorBeanImpl.class);
  }

  @Test
  public void testGetId() {
    ObjectNode jsonNode = JsonNodeFactory.instance.objectNode();

    try {
      deserializer.getId(jsonNode);
      fail("should throw RuntimeException");
    } catch (RuntimeException e) {
      assertThat(e.getMessage(), notNullValue());
    }

    jsonNode.put(AbstractBeanDeserializer.FIELD_ID, Boolean.TRUE);

    try {
      deserializer.getId(jsonNode);
      fail("should throw RuntimeException");
    } catch (RuntimeException e) {
      assertThat(e.getMessage(), notNullValue());
    }

    jsonNode.put(AbstractBeanDeserializer.FIELD_ID, "beanA");

    String id = deserializer.getId(jsonNode);
    assertThat(id, equalTo(jsonNode.get(AbstractBeanDeserializer.FIELD_ID).asText()));
  }

  @Test
  public void testGetImplClassName() {
    ObjectNode jsonNode = JsonNodeFactory.instance.objectNode();

    try {
      deserializer.getImplClassName(jsonNode);
      fail("should throw RuntimeException");
    } catch (RuntimeException e) {
      assertThat(e.getMessage(), notNullValue());
    }

    jsonNode.put(AbstractBeanDeserializer.FIELD_CLASS, Boolean.TRUE);

    try {
      deserializer.getImplClassName(jsonNode);
      fail("should throw RuntimeException");
    } catch (RuntimeException e) {
      assertThat(e.getMessage(), notNullValue());
    }

    jsonNode.put(AbstractBeanDeserializer.FIELD_CLASS, "beanA");

    String id = deserializer.getImplClassName(jsonNode);
    assertThat(id, equalTo(jsonNode.get(AbstractBeanDeserializer.FIELD_CLASS).asText()));
  }
}
