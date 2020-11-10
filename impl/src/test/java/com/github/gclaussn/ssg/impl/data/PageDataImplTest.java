package com.github.gclaussn.ssg.impl.data;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.github.gclaussn.ssg.data.PageDataNode;

public class PageDataImplTest {

  private PageDataImpl data;

  @Before
  public void setUp() {
    data = new PageDataImpl();
  }

  @Test
  public void testGet() {
    data.root.put("a", "1");
    data.root.put("b", "2");
    data.root.put("c", null);
    data.root.put("d", new LinkedList<>());
    data.root.put("e", new HashMap<>());

    PageDataNode node;

    node = data.get("a");
    assertThat(node, notNullValue());

    node = data.get("b");
    assertThat(node, notNullValue());

    node = data.get("c");
    assertThat(node.isNull(), is(true));

    node = data.get("d");
    assertThat(node, notNullValue());
    assertThat(node.isList(), is(true));

    node = data.get("e");
    assertThat(node, notNullValue());
    assertThat(node.isMap(), is(true));
  }

  @Test
  public void testFind() {
    data.find("a/b/c");

    assertThat(data.has("a"), is(true));
    assertThat(data.has("a/b"), is(true));
    assertThat(data.has("a/b/c"), is(true));

    PageDataNode node = data.get("a/b/c");
    assertThat(node, notNullValue());
    assertThat(node.isMap(), is(true));

    assertThat(data.has("d"), is(false));
    assertThat(data.has("a/d"), is(false));
    assertThat(data.has("a/b/d"), is(false));
    assertThat(data.has("a/b/c/d"), is(false));
  }

  @Test
  public void shouldGetNested() {
    Map<String, Object> y = Collections.singletonMap("z", null);
    Map<String, Object> x = Collections.singletonMap("y", y);

    data.root.put("x", x);

    PageDataNode node = data.get("x/y/z");
    assertThat(node, notNullValue());
    assertThat(node.isNull(), is(true));
  }
}
