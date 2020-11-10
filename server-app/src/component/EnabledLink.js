import React from "react";
import { Link } from "react-router-dom";

export default class EnabledLink extends React.Component {
  render() {
    const { children, enabled, to } = this.props;

    if (enabled) {
      return (<Link to={to}>{children}</Link>)
    } else {
      return (<span>{children}</span>)
    }
  }
}
