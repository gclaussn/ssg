package com.github.gclaussn.ssg.impl;

import com.github.gclaussn.ssg.PageFilter;
import com.github.gclaussn.ssg.conf.SiteConf;

class PageFilterDeserializer extends AbstractBeanDeserializer<PageFilterBeanImpl, PageFilter> {

  PageFilterDeserializer(SiteConf conf) {
    super(conf.getPageFilterTypes());
  }

  @Override
  protected PageFilterBeanImpl createBean(String id, PageFilter implementation) {
    PageFilterBeanImpl bean = new PageFilterBeanImpl();
    bean.id = id;
    bean.implementation = implementation;

    return bean;
  }
}
