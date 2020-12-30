import React from "react";

import Page from "./Page";
import PageTable from "./PageTable";

export default class PageTableView extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      pages: null
    };
  }

  componentDidMount() {
    Page.getAll().then(pages => {
      this.setState({pages: pages});
    }).catch(err => {

    });
  }

  render() {
    const { pages } = this.state;

    return (
      <div>
        <div className="p075">
          <div className="mb-3">
            <p className="h5">Pages</p>
          </div>
        </div>

        {pages !== null ? <PageTable pages={pages} /> : null}
      </div>
    )
  }
}
