import axios from "axios";

export default class SitePlugin {
  static getAll() {
    return axios.get("/api/site-plugins").then(res => {
      return res.data.map(data => new SitePlugin(data));
    }).catch(err => {
      throw err;
    });
  }

  constructor(data) {
    this._data = data;
  }

  get actions() {
    return this._data.actions;
  }

  get documentation() {
    return this._data.documentation;
  }

  get name() {
    return this._data.name;
  }

  get properties() {
    return this._data.properties;
  }

  get typeName() {
    return this._data.typeName;
  }
}
