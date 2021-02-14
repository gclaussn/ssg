package com.github.gclaussn.ssg.conf;

public interface TypeLookup<T> {

  Class<? extends T> lookup(String typeName);
}
