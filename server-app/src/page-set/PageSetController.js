import React from "react";

import PageSet from "./PageSet";

export default class PageSetController extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      pageSet: null
    };
  }

  componentDidMount() {
    this._getPageSet();
  }

  _getPageSet = () => {
    PageSet.get(this.props.pageSetId).then(pageSet => {
      this.setState({pageSet: pageSet});
    }).catch(err => {

    });
  }

  render() {
    if (this.state.pageSet === null) {
      return null;
    }

    return this.props.children({
      ...this.state,
      getPageSet: this._getPageSet
    })
  }
}
