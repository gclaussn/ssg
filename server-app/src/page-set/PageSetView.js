import React from "react";

import PageTable from "page/PageTable";

export default class PageSetView extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      pages: null
    };
  }

  componentDidMount() {
    this.props.pageSet.getPages().then(pages => {
      this.setState({pages: pages});
    }).catch(err => {

    });
  }

  render() {
    const { pageSet } = this.props;
    const { pages } = this.state;

    return (
      <div>
        <div className="p075">
          <p className="h5">Pages set: {pageSet.id}</p>
          <p>&nbsp;</p>
        </div>

        {pages !== null ? <PageTable pages={pages} /> : null}
      </div>
    )
  }
}
