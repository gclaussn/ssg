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

    return (
      <div>
        <div className="p075">
          <p className="h5">Pages set: {pageSet.id}</p>
          <p>&nbsp;</p>
        </div>

        {this._renderPages()}
      </div>
    )
  }

  _renderPages() {
    const { pages } = this.state;

    if (pages === null) {
      return null;
    }

    return <PageTable pages={pages} />
  }
}
