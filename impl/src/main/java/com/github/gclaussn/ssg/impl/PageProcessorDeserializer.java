package com.github.gclaussn.ssg.impl;

import com.github.gclaussn.ssg.PageProcessor;
import com.github.gclaussn.ssg.conf.SiteConf;

class PageProcessorDeserializer extends AbstractBeanDeserializer<PageProcessorBeanImpl, PageProcessor> {

  PageProcessorDeserializer(SiteConf conf) {
    super(conf.getPageProcessorTypes());
  }

  @Override
  protected PageProcessorBeanImpl createBean(String id, PageProcessor implementation) {
    PageProcessorBeanImpl bean = new PageProcessorBeanImpl();
    bean.id = id;
    bean.implementation = implementation;

    return bean;
  }
}
