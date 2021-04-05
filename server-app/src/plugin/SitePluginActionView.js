import React from "react";
import { Link } from "react-router-dom";

import SitePluginAction from "./SitePluginAction";
import SitePluginActionTask from "./SitePluginActionTask";

import TextPropertyInput from "./property/TextPropertyInput";

export default class SitePluginActionView extends React.Component {
  constructor(props) {
    super(props);

    const { match } = this.props;

    this._typeName = match.params.typeName.replaceAll("/", ".");
    // special action, which should be excluded from execution
    this._disabled = this._typeName === "com.github.gclaussn.ssg.server.StartAction";

    this.state = {
      pluginAction: null,
      pluginActionTask: null
    };

    this._pluginActionTaskRef = React.createRef();
    this._lastScrollTop = window.pageYOffset || document.documentElement.scrollTop;
  }

  componentDidMount() {
    const { match } = this.props;

    SitePluginAction.get(match.params.typeName).then(pluginAction => {
      this.setState({pluginAction: pluginAction});
    }).catch(err => {

    });

    window.addEventListener("scroll", this._handleScrollTop);
  }

  componentWillUnmount() {
    window.removeEventListener("scroll", this._handleScrollTop);
  }

  _handleClickExecute = (e) => {
    this.state.pluginAction.execute().then(url => {
      // enable auto scrolling
      this._autoScroll = true;

      // connect to plugin action task
      this.setState({pluginActionTask: new SitePluginActionTask(url, this._handleTaskUpdate)});
    }).catch(err => {

    });
  }
  _handleTaskUpdate = () => {
    this.forceUpdate(() => {
      if (this._autoScroll) {
        this._pluginActionTaskRef.current.scrollIntoView(false);
      }
    });
  }
  _handleScrollTop = () => {
    const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
    if (scrollTop <= this._lastScrollTop) {
      // disable auto scrolling, if scrolled upwards
      this._autoScroll = false;
    }

    // update scroll top value
    this._lastScrollTop = scrollTop <= 0 ? 0 : scrollTop;
  }

  render() {
    const { pluginAction, pluginActionTask } = this.state;

    return (
      <div>
        <div className="p075">
          <div className="row">
            <div className="col-9">
              <p className="h5">
                Plugin action: {this._typeName}
              </p>
            </div>
            <div className="col-3 text-right">
              <Link className="btn btn-secondary" to={"/plugins"}>Back</Link>
            </div>
          </div>
        </div>

        {this._renderForm(pluginAction)}

        <div ref={this._pluginActionTaskRef}>
          {this._renderTask(pluginActionTask)}
        </div>
      </div>
    )
  }

  _renderForm(pluginAction) {
    if (pluginAction === null) {
      return null;
    }

    return (
      <div className="p075">
        <h6 className="card-subtitle mb-2 text-muted">
          {pluginAction.name}
        </h6>
        <span className="card-text">{pluginAction.documentation}</span>

        <form className="pt-4">
          {pluginAction.properties.map(property => this._renderProperty(property))}

          <div className="pt-3 text-center">
            <button
              className="btn btn-primary"
              disabled={this._disabled}
              onClick={this._handleClickExecute}
              type="button"
            >
              Execute
            </button>
          </div>
        </form>
      </div>
    )
  }
  _renderProperty(property) {
    return (
      <div key={property.name} className="form-group">
        <label>
          {property.name}
          {property.required ? "*" : null}
        </label>

        {this._renderPropertyInput(property)}
        
        <small className="form-text text-muted">{property.documentation}</small>
      </div>
    )
  }
  _renderPropertyInput(property) {
    if (property.isEnum()) {
      return null;
    } else {
      return <TextPropertyInput property={property} disabled={this._disabled} />
    }
  }

  _renderTask(pluginActionTask) {
    if (pluginActionTask === null) {
      return null;
    }

    return (
      <div className="container-fluid mb-4 bg-light">
        <pre className="pt-2 pb-2">
          <code>
            {pluginActionTask.messages.map((message, index) => {
              return message.length === 0 ? <br key={index} /> : <span key={index}>{message}</span>
            })}
          </code>
        </pre>

        {this._renderTaskSpinner(pluginActionTask)}
      </div>
    )
  }
  _renderTaskSpinner(pluginActionTask) {
    if (pluginActionTask.done) {
      return null;
    }

    return (
      <div className="pb-2 text-center">
        <div className="spinner-border text-primary">
          <span className="sr-only">Loading...</span>
        </div>
      </div>
    )
  }
}
