import axios from "axios";

import Page from "page/Page";

export default class PageSet {
  static get(id) {
    return axios.get(`/api/page-sets/${id}`).then(res => {
      return new PageSet(res.data);
    }).catch(err => {
      throw err;
    });
  }

  static getAll() {
    return axios.get("/api/page-sets").then(res => {
      return res.data.map(data => new PageSet(data));
    }).catch(err => {
      throw err;
    });
  }

  constructor(data) {
    this._data = data;
  }

  getPages() {
    return axios.get(`/api/page-sets/${this.id}/pages`).then(res => {
      return res.data.map(data => new Page(data));
    }).catch(err => {
      throw err;
    });
  }

  get id() {
    return this._data.id;
  }
  get skipped() {
    return this._data.skipped;
  }
}
