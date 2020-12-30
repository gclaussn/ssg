import React from "react";
import { Link } from "react-router-dom";
import { withRouter } from "react-router";

class AppNav extends React.Component {
  constructor(props) {
    super(props);

    this.links = [
      {label: "Pages", to: "/pages", icon: "fas fa-file-code"},
      {label: "Page sets", to: "/page-sets", icon: "far fa-copy"},
      {label: "Plugins", to: "/plugins", icon: "fas fa-plug"}
    ];
  }

  _shouldHide(path) {
    return path.startsWith("/pages/");
  }

  render() {
    const { history } = this.props;

    if (this._shouldHide(history.location.pathname)) {
      return null;
    }

    return (
      <div className="col-12 col-md-2 p-0 bg-dark position-fixed h-100">
        <div className="navbar navbar-expand navbar-dark bg-dark flex-md-column flex-row align-items-start">
          <div className="collapse navbar-collapse">
            <ul className="flex-md-column flex-row navbar-nav w-100 justify-content-between">
              <li className="nav-item d-sm-none d-md-inline">
                <h6 className="text-white"><i>Static Site Generator</i></h6>
              </li>

              {this.links.map((link, index) => this._renderLink(link, index))}
            </ul>
          </div>
        </div>
      </div>
    )
  }

  _renderLink(link, index) {
    return (
      <li key={index} className="nav-item w-100">
        <Link className="nav-link" to={link.to}>
          <i className={link.icon + " mr-1"} style={{width: "14px"}}></i> <span>{link.label}</span>
        </Link>
      </li>
    )
  }
}

export default withRouter(AppNav);
