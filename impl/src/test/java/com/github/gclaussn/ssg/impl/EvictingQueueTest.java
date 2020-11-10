package com.github.gclaussn.ssg.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class EvictingQueueTest {

  private EvictingQueue<Integer> queue;

  @Before
  public void setUp() {
    queue = new EvictingQueue<>(8);
  }

  @Test
  public void testAdd() {
    for (int i = 0; i < queue.capacity; i++) {
      queue.add(Integer.valueOf(i));
    }

    assertThat(queue.elements.getFirst(), is(Integer.valueOf(0)));
    assertThat(queue.elements.getLast(), is(Integer.valueOf(7)));

    queue.add(Integer.valueOf(8));
    queue.add(Integer.valueOf(9));

    assertThat(queue.elements.getFirst(), is(Integer.valueOf(2)));
    assertThat(queue.elements.getLast(), is(Integer.valueOf(9)));
  }

  @Test
  public void testTakeWhile() {
    queue.add(Integer.valueOf(7));
    queue.add(Integer.valueOf(8));
    queue.add(Integer.valueOf(9));

    List<Integer> results = queue.takeWhile(value -> value >= 8);
    assertThat(results.size(), is(2));
    assertThat(results.get(0), is(9));
    assertThat(results.get(1), is(8));
  }
}
