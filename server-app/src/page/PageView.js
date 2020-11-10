import React from "react";
import { Link } from "react-router-dom";

export default class PageView extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      timestamp: -1
    };

    this.pageRef = React.createRef();
  }

  componentWillMount() {
    document.body.style["overflow-y"] = "hidden";
  }

  componentDidMount() {
    this._doReload();

    const { eventStream, page } = this.props;

    // subscribe event stream
    this._subscription = eventStream.subscribe(this._onEvent, {
      id: "page-events",
      filters: {sources: [page.id], types: ["GENERATE_PAGE"]}
    });
  }

  componentWillUnmount() {
    this.props.eventStream.unsubscribe(this._subscription);

    document.body.style["overflow-y"] = "scroll";
  }

  _doReload = () => {
    this.props.getPage();
    this.setState({timestamp: Date.now()});
  }

  _onEvent = (event) => {
    if (event.hasError()) {
      return;
    }

    this._doReload();
  }

  _onPageLoad = () => {
    var iframe = this.pageRef.current;

    const fadeIn = "page-fade-in";
    if (!iframe.classList.contains(fadeIn)) {
      // fade in, if iframe has been reloaded
      iframe.classList.add(fadeIn);
    }
  }

  render() {
    const src = this.props.getPageUrl();

    return (
      <div>
        {this._renderActions(src)}
        {this._renderPage(src)}
      </div>
    )
  }

  _renderActions(src) {
    const { page } = this.props;

    return (
      <div className="page-actions">
        <div className="btn-group-vertical" style={{width: "55px"}}>
          <Link to={`/pages/${page.id}/data`} className="btn btn-info rounded-0" title="View page data">
            <i className="far fa-file" />
          </Link>
          <button onClick={this._doReload} type="button" className="btn btn-info rounded-0" title="Reload page">
            <i className="fas fa-redo-alt" />
          </button>
          <a href={src} target="_blank" rel="noopener noreferrer" className="btn btn-info rounded-0" title="Open page in separate tab">
            <i className="fas fa-external-link-alt" />
          </a>
          <Link to={"/pages"} className="btn btn-info rounded-0" title="Back">
            <i className="fas fa-arrow-left" />
          </Link>
        </div>
      </div>
    )
  }

  _renderPage(src) {
    const { timestamp } = this.state;

    return timestamp !== -1 ? (
      <iframe
        key={timestamp}
        src={`${src}?v=${timestamp}`}
        title="page"
        className="page"
        ref={this.pageRef}
        onLoad={this._onPageLoad}
      />
     ) : null;
  }
}
