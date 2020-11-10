import axios from "axios";

export default class Site {
  static get() {
    return axios.get("/api/site").then(res => {
      return new Site(res.data);
    }).catch(err => {
      throw err;
    });
  }

  constructor(data) {
    this._data = data;
  }
}
