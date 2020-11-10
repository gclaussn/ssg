import React from "react";
import { Link } from "react-router-dom";
import { withRouter } from "react-router";

class AppNav extends React.Component {
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
              <li className="nav-item w-100">
                <Link className="nav-link" to="/pages">
                  <i className="fas fa-file-code mr-1" style={{width: "14px"}}></i> <span>Pages</span>
                </Link>
              </li>
              <li className="nav-item w-100">
                <Link className="nav-link" to="/page-sets">
                  <i className="far fa-copy mr-1" style={{width: "14px"}}></i> <span>Page sets</span>
                </Link>
              </li>
            </ul>
          </div>
        </div>
      </div>
    )
  }
}

export default withRouter(AppNav);
