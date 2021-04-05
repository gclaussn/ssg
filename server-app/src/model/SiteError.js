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

  get cause() {
    return this._data.cause;
  }
  get location() {
    return this._data.location;
  }
  get message() {
    return this._data.message;
  }
  get sourceCode() {
    return this._data.sourceCode;
  }
  get stackTrace() {
    return this._data.stackTrace;
  }
  get type() {
    return this._data.type;
  }
}
