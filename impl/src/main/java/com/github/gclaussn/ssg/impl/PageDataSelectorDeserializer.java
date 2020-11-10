package com.github.gclaussn.ssg.impl;

import com.github.gclaussn.ssg.conf.SiteConf;
import com.github.gclaussn.ssg.data.PageDataSelector;

class PageDataSelectorDeserializer extends AbstractBeanDeserializer<PageDataSelectorBeanImpl, PageDataSelector> {

  PageDataSelectorDeserializer(SiteConf conf) {
    super(conf.getPageDataSelectorTypes());
  }

  @Override
  protected PageDataSelectorBeanImpl createBean(String id, PageDataSelector implementation) {
    PageDataSelectorBeanImpl bean = new PageDataSelectorBeanImpl();
    bean.id = id;
    bean.implementation = implementation;

    return bean;
  }
}
