import axios from "axios";

import SiteError from "model/SiteError";

export default class SiteEvent {
  static getBySourceId(sourceId, from = -1) {
    return axios.get(`/api/site-events/${sourceId}?from=${from}`).then(res => {
      return res.data.map(event => new SiteEvent(event));
    }).catch(err => {
      throw err;
    });
  }

  constructor(data) {
    this._data = data;
  }

  hasError() {
    return this._data.error !== null;
  }

  get error() {
    return new SiteError(this._data.error);
  }
  get sourceId() {
    return this._data.sourceId;
  }
  get sourceType() {
    return this._data.sourceType;
  }
  get timestamp() {
    return this._data.timestamp;
  }
  get type() {
    return this._data.type;
  }
}
