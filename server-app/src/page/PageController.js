import React from "react";
import { Link } from "react-router-dom";

import SiteErrorViewer from "component/SiteErrorViewer";

import { eventStream } from "service/SiteEventStream";

import Page from "./Page";

export default class PageController extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      code: null,
      error: null,
      page: null,
      pageData: null
    };
  }

  componentDidMount() {
    this._getPage();

    // subscribe event stream and choose appropriate event handler
    this._subscription = eventStream.subscribe(this.props.data ? this._onPageDataEvent : this._onPageEvent, {
      id: "page-error-events",
      filters: {sources: [this.props.pageId]}
    });
  }

  componentWillUnmount() {
    eventStream.unsubscribe(this._subscription);
  }

  _onPageEvent = (event) => {
    if (event.hasError()) {
      // handle all errors
      this._handleError(event.error);
    } else if (event.type === "GENERATE_PAGE") {
      // refresh when page has been generated
      this._getPage();
    }
  }

  _onPageDataEvent = (event) => {
    if (event.hasError() && event.type !== "GENERATE_PAGE") {
      // handle all but GENERATE_PAGE errors
      this._handleError(event.error);
    } else if (event.type === "LOAD_PAGE") {
      // refresh when page has been loaded
      this._getPage();
    }
  }

  _handleError(error) {
    this.setState({page: null});

    if (error.location === null) {
      this.setState({error: error, code: null});
      return;
    }

    error.getCode().then(code => {
      this.setState({error: error, code: code});
    }).catch(err => {

    });
  }

  _getPage = () => {
    Page.get(this.props.pageId).then(page => {
      this.setState({page: page, error: null});
    }).catch(err => {

    });
  }

  _getPageData = () => {
    Page.getData(this.props.pageId).then(pageData => {
      this.setState({pageData: pageData});
    }).catch(err => {

    });
  }

  _getPageUrl = () => {
    const { server } = this.props;
    return `http://${server.host}:${server.port}${this.state.page.url}`;
  }

  render() {
    const { error, page } = this.state;

    if (error !== null) {
      return this._renderPageError();
    }
    if (page === null) {
      return this._renderPageNotFound();
    }

    return this.props.children({
      ...this.state,
      eventStream: eventStream,
      getPage: this._getPage,
      getPageData: this._getPageData,
      getPageUrl: this._getPageUrl
    });
  }

  _renderPageError() {
    const { code, error } = this.state;

    return (
      <div>
        <div className="page-actions">
          <Link to={"/pages"} className="btn btn-info rounded-0" title="Back">
            <i className="fas fa-arrow-left" />
          </Link>
        </div>
        <SiteErrorViewer code={code} error={error} />
      </div>
    )
  }

  _renderPageNotFound() {
    return null;
  }
}
