import React from "react";

import EnabledLink from "component/EnabledLink";
import Icon from "component/Icon";

export default class PageTable extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      filtered: props.pages,
      filter: ""
    };
  }

  _handleFilterChanged = (e) => {
    const { pages } = this.props;

    this.setState({
      filter: e.target.value,
      filtered: pages.filter(page => page.id.includes(e.target.value))
    });
  }

  render() {
    const { filter, filtered } = this.state;

    return (
      <div>
        <div className="p075">
          <div className="form-group">
            <input className="form-control" placeholder="Filter by ID" type="text" value={filter} onChange={this._handleFilterChanged} />
          </div>
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
              <td className="border-top-0 text-center">
                <b>Rejected</b>
              </td>
            </tr>
          </thead>

          <tbody>
            {filtered.map(page => this._renderPage(page))}
          </tbody>
        </table>
      </div>
    )
  }

  _renderPage(page) {
    return (
      <tr key={page.id}>
        <td>
          <EnabledLink enabled={!page.skipped && !page.rejected} to={`/pages/${page.id}`}>{page.id}</EnabledLink>
        </td>
        <td className="text-center">
          <Icon className="fas fa-ban text-danger" visible={page.skipped} />
        </td>
        <td className="text-center">
          <Icon className="fas fa-ban text-danger" visible={page.rejected} />
        </td>
      </tr>
    )
  }
}
