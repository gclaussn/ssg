import axios from "axios";

import SiteProperty from "./SiteProperty";

export default class SitePluginAction {
  static get(typeName) {
    return axios.get(`/api/site-plugins/actions/${typeName}`).then(res => {
      return new SitePluginAction(typeName, res.data);
    }).catch(err => {
      throw err;
    });
  }

  constructor(typeName, data) {
    this._typeName = typeName;
    this._data = data;
    this._properties = data.properties.map(property => new SiteProperty(property));
  }

  execute() {
    const data = {};

    this.properties.forEach(property => {
      if (property.value.length > 0) {
        data[property.name] = property.value;
      }
    });

    return axios.post(`/api/site-plugins/actions/${this._typeName}/execute`, data).then(res => {
      return res.data;
    }).catch(err => {
      throw err;
    });
  }

  get documentation() {
    return this._data.documentation;
  }

  get id() {
    return this._data.id;
  }

  get name() {
    const { id, name } = this._data;

    if (name && id) {
      return `${name} (${id})`;
    } else {
      return name || id;
    }
  }

  get properties() {
    return this._properties;
  }

  get typeName() {
    return this._data.typeName;
  }
}
