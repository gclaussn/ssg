import { w3cwebsocket as W3CWebSocket } from "websocket";

export default class SitePluginGoalTask {
  constructor(url, update) {
    const client = new W3CWebSocket(url);

    client.onopen = () => {
      
    };
    client.onclose = () => {
      this._done = true;
      
      update();
    };
    client.onerror = () => {

    };

    client.onmessage = (message) => {
      for (const line of message.data.split("\n")) {
        if (line.length > 0) {
          this.messages.push(line);
        }
        this.messages.push("");
      }

      update();
    };

    this._client = client;
    this._messages = [];
  }

  get done() {
    return this._done || false;
  }

  get messages() {
    return this._messages;
  }
}
