import React from "react";

import EnabledLink from "component/EnabledLink";
import Icon from "component/Icon";

import PageSet from "./PageSet";

export default class PageSetTableView extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      pageSets: null
    };
  }

  componentDidMount() {
    PageSet.getAll().then(pageSets => {
      this.setState({pageSets: pageSets});
    }).catch(err => {

    });
  }

  render() {
    const { pageSets } = this.state;

    if (pageSets === null) {
      return null;
    }

    return (
      <div>
        <div className="p075">
          <p className="h5">Pages sets</p>
          <p>&nbsp;</p>
        </div>

        <table className="table">
          <thead>
            <tr>
              <td className="border-top-0">
                <b>ID</b>
              </td>
              <td className="border-top-0 text-center">
                <b>Skipped</b>
              </td>
            </tr>
          </thead>

          <tbody>
            {pageSets.map(pageSet => this._renderPageSet(pageSet))}
          </tbody>
        </table>
      </div>
    )
  }

  _renderPageSet(pageSet) {
    return (
      <tr key={pageSet.id}>
        <td>
          <EnabledLink enabled={!pageSet.skipped} to={`/page-sets/${pageSet.id}`}>{pageSet.id}</EnabledLink>
        </td>
        <td className="text-center">
          <Icon className="fas fa-ban text-danger" visible={pageSet.skipped} />
        </td>
      </tr>
    )
  }
}