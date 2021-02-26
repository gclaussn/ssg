package com.github.gclaussn.ssg.impl.model;

import com.github.gclaussn.ssg.PageFilter;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.impl.conf.TypeLookupImpl;

class PageFilterDeserializer extends AbstractBeanDeserializer<PageFilterBeanImpl, PageFilter> {

  PageFilterDeserializer(Site site) {
    super(site, new TypeLookupImpl<>(site.getConf().getPageFilterTypes()));
  }

  @Override
  protected PageFilterBeanImpl createBean(String id, PageFilter filter) {
    PageFilterBeanImpl bean = new PageFilterBeanImpl(site);
    bean.id = id;
    bean.impl = filter;

    return bean;
  }
}
