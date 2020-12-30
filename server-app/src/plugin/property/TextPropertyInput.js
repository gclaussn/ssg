import React from "react";

export default class TextPropertyInput extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      value: props.property.value || ""
    };
  }

  _handleChange = (e) => {
    this.props.property.value = e.target.value;
    this.setState({value: e.target.value});
  }

  render() {
    const { property } = this.props;

    return (
      <input
        className="form-control"
        disabled={this.props.disabled}
        onChange={this._handleChange}
        placeholder={`Type: ${property.type}`}
        type={property.masked ? "password" : "text"}
        value={this.state.value}
      />
    )
  }
}
