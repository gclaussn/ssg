package com.github.gclaussn.ssg.impl;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Predicate;

class EvictingQueue<T> {

  protected final Deque<T> elements;

  protected final int capacity;

  EvictingQueue(int capacity) {
    this.capacity = capacity;
    this.elements = new ConcurrentLinkedDeque<>();
  }

  protected void add(T value) {
    if (elements.size() >= capacity) {
      elements.remove();
    }
    elements.add(value);
  }

  protected void clear() {
    elements.clear();
  }

  protected Iterator<T> iterator() {
    return elements.descendingIterator();
  }

  protected List<T> takeAll() {
    List<T> list = new LinkedList<>();
    iterator().forEachRemaining(list::add);
    return list;
  }

  protected List<T> takeWhile(Predicate<T> predicate) {
    List<T> list = new LinkedList<>();

    Iterator<T> it = iterator();
    while (it.hasNext()) {
      T element = it.next();

      if (!predicate.test(element)) {
        break;
      }

      list.add(element);
    }

    return list;
  }
}
