package com.github.gclaussn.ssg.impl.model;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.data.PageDataSelector;

class PageDataSelectorDeserializer extends AbstractBeanDeserializer<PageDataSelectorBeanImpl, PageDataSelector> {

  PageDataSelectorDeserializer(Site site) {
    super(site, site.getConf().getPageDataSelectorTypes());
  }

  @Override
  protected PageDataSelectorBeanImpl createBean(String id, PageDataSelector dataSelector) {
    PageDataSelectorBeanImpl bean = new PageDataSelectorBeanImpl(site);
    bean.id = id;
    bean.impl = dataSelector;

    return bean;
  }
}
