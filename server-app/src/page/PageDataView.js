import React from "react";
import { Link } from "react-router-dom";
import ReactJson from "react-json-view";

export default class PagePreview extends React.Component {
  componentDidMount() {
    this._doReload();

    const { eventStream, page } = this.props;

    // subscribe event stream
    this._subscription = eventStream.subscribe(this._onEvent, {
      id: "page-events",
      filters: {sources: [page.id], types: ["LOAD_PAGE"]}
    });
  }

  componentWillUnmount() {
    this.props.eventStream.unsubscribe(this._subscription);
  }

  _doReload = () => {
    this.props.getPageData();
  }

  _onEvent = (event) => {
    if (!event.path && event.hasError()) {
      return;
    }

    this._doReload();
  }

  render() {
    return (
      <div>
        {this._renderActions()}
        {this._renderPageData()}
      </div>
    )
  }

  _renderActions() {
    const { page } = this.props;

    return (
      <div className="page-actions">
        <div className="btn-group-vertical" style={{width: "55px"}}>
          <Link to={`/pages/${page.id}`} className="btn btn-info rounded-0" title="View page">
            <i className="far fa-eye" />
          </Link>
          <button onClick={this._doReload} type="button" className="btn btn-info rounded-0" title="Reload page data">
            <i className="fas fa-redo-alt" />
          </button>
          <a href={this.props.getPageUrl()} target="_blank" rel="noopener noreferrer" className="btn btn-info rounded-0" title="Open page in separate tab">
            <i className="fas fa-external-link-alt" />
          </a>
          <Link to={"/pages"} className="btn btn-info rounded-0" title="Back">
            <i className="fas fa-arrow-left" />
          </Link>
        </div>
      </div>
    )
  }

  _renderPageData() {
    const { pageData } = this.props;

    if (pageData === null) {
      return null;
    }

    return (
      <div>
        <div className="p075">
          <p className="h5">Page data</p>
        </div>

        <ReactJson
          collapsed={3}
          displayDataTypes={false}
          enableClipboard={false}
          name={false}
          sortKeys={true}
          src={pageData}
        />
      </div>
    )
  }
}
