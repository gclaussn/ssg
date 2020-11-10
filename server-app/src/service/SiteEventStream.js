import { w3cwebsocket as W3CWebSocket } from "websocket";

import SiteEvent from "model/SiteEvent";

class Subscription {
  constructor(callback, options) {
    this._callback = callback;
    this._id = options.id;

    const filters = options.filters || {};

    this._filterTypes = new Set(filters.types || []);
    this._filterSources = new Set(filters.sources || []);
  }

  _filter(filter, value) {
    return filter.size === 0 || filter.has(value);
  }

  onEvent(event) {
    if (!this._filter(this._filterTypes, event.type)) {
      return;
    }
    if (!this._filter(this._filterSources, event.sourceId)) {
      return;
    }

    this._callback(event);
  }

  get id() {
    return this._id;
  }
}

class SiteEventStream {
  constructor() {
    this._subscriptions = [];
  }

  start(options) {
    const client = new W3CWebSocket(`ws://${options.host}:${options.port}/wsa/events`);

    client.onopen = () => {

    };
    client.onclose = () => {

    };
    client.onerror = () => {

    };

    client.onmessage = (message) => {
      const event = new SiteEvent(JSON.parse(message.data));

      this._subscriptions.forEach(subscription => subscription.onEvent(event));
    };
  }

  subscribe(callback, options) {
    this._subscriptions.push(new Subscription(callback, options));

    return options.id;
  }

  unsubscribe(subscriptionId) {
    this._subscriptions = this._subscriptions.filter(subscription => subscription.id !== subscriptionId);
  }
}

const eventStream = new SiteEventStream();

export { eventStream };
