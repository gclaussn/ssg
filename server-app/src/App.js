import React from "react";
import { Route } from "react-router-dom";
import { Switch, withRouter } from "react-router";

import Server from "server/Server";

import { eventStream } from "service/SiteEventStream";

import PageController from "page/PageController";
import PageDataView from "page/PageDataView";
import PageTableView from "page/PageTableView";
import PageView from "page/PageView";

import PageSetController from "page-set/PageSetController";
import PageSetTableView from "page-set/PageSetTableView";
import PageSetView from "page-set/PageSetView";

import SitePluginView from "plugin/SitePluginView";
import SitePluginActionView from "plugin/SitePluginActionView";

import StopView from "server/StopView";

class App extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      server: null
    };
  }

  componentDidMount() {
    Server.get().then(server => {
      this.setState({server: server});
      
      eventStream.start({host: server.host, port: server.port});
    }).catch(err => {

    });
  }

  render() {
    if (this.state.server === null) {
      return null;
    }

    return (
      <div className="app col offset-md-2 offset-sm-0">
        <Switch>
          <Route exact path="/pages" component={PageTableView} />
          <Route exact path="/page-sets" component={PageSetTableView} />
          <Route exact path="/plugins" component={SitePluginView} />
          <Route exact path="/stop" component={StopView} />

          <Route path="/pages/:pageId+/data" render={this._renderPageDataView} />
          <Route path="/pages/:pageId+" render={this._renderPageView} />
          <Route path="/page-sets/:pageSetId+" render={this._renderPageSetView} />
          <Route path="/plugin-actions/:typeName+" component={SitePluginActionView} />
          <Route component={PageTableView} />
        </Switch>
      </div>
    )
  }

  _renderPageDataView = (props) => {
    const pageId = props.match.params.pageId;

    return (
      <PageController key={props.location.key} pageId={pageId} server={this.state.server} data={true}>
        {props => <PageDataView {...props} />}
      </PageController>
    )
  }
  _renderPageView = (props) => {
    const pageId = props.match.params.pageId;

    return (
      <PageController key={props.location.key} pageId={pageId} server={this.state.server}>
        {props => <PageView {...props} />}
      </PageController>
    )
  }

  _renderPageSetView = (props) => {
    const pageSetId = props.match.params.pageSetId;

    return (
      <PageSetController key={pageSetId} pageSetId={pageSetId}>
        {props => <PageSetView {...props} />}
      </PageSetController>
    )
  }
}

export default withRouter(App);
