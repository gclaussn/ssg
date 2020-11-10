import React from "react";

export default class Icon extends React.Component {
  render() {
    const { className, visible } = this.props;
    return visible ? <i className={className}></i> : "-";
  }
}
