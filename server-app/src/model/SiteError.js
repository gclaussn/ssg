import axios from "axios";

export default class SitError {
  constructor(data) {
    this._data = data;
  }

  getFileType() {
    switch (this.type) {
      case "BEAN":
      case "MODEL":
        return "yaml";
      case "TEMPLATE":
        return "jade";
      default:
        return;
    }
  }

  getCode() {
    const line = this.location.line;

    const from = line > 10 ? (line - 10) : 0;

    return axios.get(`/api/sources/${this._data.sourceId}/${this.getFileType()}?from=${from}&to=${line}`).then(res => {
      return res.data;
    }).catch(err => {
      throw err;
    });
  }

  get cause() {
    return this._data.cause;
  }
  get location() {
    return this._data.location;
  }
  get message() {
    return this._data.message;
  }
  get stackTrace() {
    return this._data.stackTrace;
  }
  get type() {
    return this._data.type;
  }
}
