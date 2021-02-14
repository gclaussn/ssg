package com.github.gclaussn.ssg.impl.model;

import com.github.gclaussn.ssg.PageProcessor;
import com.github.gclaussn.ssg.Site;

class PageProcessorDeserializer extends AbstractBeanDeserializer<PageProcessorBeanImpl, PageProcessor> {

  PageProcessorDeserializer(Site site) {
    super(site, site.getConf().getPageProcessorTypes());
  }

  @Override
  protected PageProcessorBeanImpl createBean(String id, PageProcessor implementation) {
    PageProcessorBeanImpl bean = new PageProcessorBeanImpl(site);
    bean.id = id;
    bean.impl = implementation;

    return bean;
  }
}
