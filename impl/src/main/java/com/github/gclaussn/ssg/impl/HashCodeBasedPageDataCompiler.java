package com.github.gclaussn.ssg.impl;

import java.util.HashMap;
import java.util.Map;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.PageInclude;
import com.github.gclaussn.ssg.data.PageData;
import com.github.gclaussn.ssg.data.PageDataBuilder;
import com.github.gclaussn.ssg.data.PageDataSelectorBean;

class HashCodeBasedPageDataCompiler extends PageDataCompiler {

  private final Map<HashCode, HashCode> hashCodes;

  HashCodeBasedPageDataCompiler() {
    hashCodes = new HashMap<>();
  }

  @Override
  public PageData apply(Page page) {
    PageDataBuilder builder = PageData.builder();

    // put page data
    builder.putRoot(page.getData());

    // put data from page includes
    for (PageInclude pageInclude : resolvePageIncludes(page)) {
      String id = normalizeId(pageInclude.getId());
      builder.put(id, pageInclude.getData().getRootMap());
    }

    boolean dependentDataChanged = false;

    // put data from selectors
    for (PageDataSelectorBean dataSelector : page.getDataSelectors()) {
      Object result = dataSelector.select(page);

      HashCode newHashCode = new HashCode(page.getId(), dataSelector.getId(), result.hashCode());
      HashCode oldHashCode = hashCodes.get(newHashCode);

      // if there is no old hash code or the value is different
      // the dependent data must have changed
      if (oldHashCode == null || oldHashCode.getValue() != newHashCode.getValue()) {
        hashCodes.put(newHashCode, newHashCode);
        dependentDataChanged = true;
      }

      String id = normalizeId(dataSelector.getId());
      builder.putIfAbsent(id, result);
    }

    if (!dependentDataChanged) {
      // indicate that the data has not changed
      return null;
    }

    return builder.build();
  }
}
