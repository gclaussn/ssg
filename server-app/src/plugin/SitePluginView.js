import React from "react";

import SitePlugin from "./SitePlugin";

export default class SitePluginView extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      plugins: []
    }
  }

  componentDidMount() {
    SitePlugin.getAll().then(plugins => {
      this.setState({plugins: plugins});
    }).catch(err => {

    });
  }

  _handlePluginActionClick(pluginAction) {
    this.props.history.push(`/plugin-actions/${pluginAction.replaceAll(".", "/")}`);
  }

  render() {
    const { plugins } = this.state;

    return (
      <div>
        <div className="p075">
          <p className="h5">Plugins</p>
          <p>&nbsp;</p>
        </div>

        <div className="container-fluid">
          <div className="row">
            {plugins.map(plugin => this._renderPlugin(plugin))}
          </div>
        </div>
      </div>
    )
  }

  _renderPlugin(plugin) {
    return (
      <div key={plugin.typeName} className="col-6 mb-4">
        <div className="card h-100">
          <div className="card-body">
            <h6 className="card-title">{plugin.typeName}</h6>
            <h6 className="card-subtitle mb-2 text-muted">{plugin.name}</h6>
            <span className="card-text">{plugin.documentation}</span>

            {this._renderPluginActions(plugin)}
            {this._renderProperties(plugin)}
          </div>
        </div>
      </div>
    )
  }

  _renderPluginActions(plugin) {
    if (plugin.actions.length === 0) {
      return null;
    }

    return (
      <div className="pt-3">
        <span>Actions:</span>
        <div className="pt-1 list-group">
          {plugin.actions.map(pluginAction => this._renderPluginAction(pluginAction))}
        </div>
      </div>
    )
  }
  _renderPluginAction(pluginAction) {
    const onClick = this._handlePluginActionClick.bind(this, pluginAction);

    return (
      <button
        key={pluginAction}
        type="button"
        className="list-group-item list-group-item-action"
        onClick={onClick}
      >
        {pluginAction}
      </button>
    )
  }

  _renderProperties(plugin) {
    if (plugin.properties.length === 0) {
      return null;
    }

    return (
      <div className="pt-3">
        <span>Properties:</span>
      </div>
    )
  }
  _renderProperty(property) {
    return null;
  }
}
