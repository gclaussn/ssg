package com.github.gclaussn.ssg.data;

import java.util.Map;

public interface PageDataBuilder {

  PageData build();

  PageDataBuilder put(String location, Object data);

  PageDataBuilder putIfAbsent(String location, Object data);

  PageDataBuilder putRoot(Map<String, Object> data);

  PageDataBuilder putRoot(PageData data);
}
