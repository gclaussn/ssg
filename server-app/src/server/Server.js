import axios from "axios";

export default class Server {
  static get() {
    return axios.get("/api/server").then(res => {
      return new Server(res.data);
    }).catch(err => {
      throw err;
    });
  }

  static stop() {
    return axios.get("/api/server/stop").then(res => {
      return res.status;
    }).catch(err => {
      throw err;
    });
  }

  constructor(data) {
    this._data = data;
  }

  get host() {
    return this._data.host;
  }
  get port() {
    return this._data.port;
  }
}
