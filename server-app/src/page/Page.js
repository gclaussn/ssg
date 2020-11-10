import axios from "axios";

export default class Page {
  static get(id) {
    return axios.get(`/api/pages/${id}`).then(res => {
      return new Page(res.data);
    }).catch(err => {
      throw err;
    });
  }

  static getAll() {
    return axios.get("/api/pages").then(res => {
      return res.data.map(data => new Page(data));
    }).catch(err => {
      throw err;
    });
  }

  static getData(id) {
    return axios.get(`/api/pages/${id}/data`).then(res => {
      return res.data;
    }).catch (err => {
      throw err;
    });
  }

  constructor(data) {
    this._data = data;
  }

  get id() {
    return this._data.id;
  }
  get rejected() {
    return this._data.rejected;
  }
  get skipped() {
    return this._data.skipped;
  }
  get url() {
    return this._data.url;
  }
}
