import React from "react";

import { PrismLight as SyntaxHighlighter } from "react-syntax-highlighter";
import style from "react-syntax-highlighter/dist/esm/styles/prism/material-light";

import { pug, yaml } from "react-syntax-highlighter/dist/esm/languages/prism";

SyntaxHighlighter.registerLanguage("pug", pug);
SyntaxHighlighter.registerLanguage("yaml", yaml);

export default class SiteErrorViewer extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      showStackTrace: props.error.location ? false : true
    }
  }

  _toggleStackTrace = () => {
    this.setState({showStackTrace: !this.state.showStackTrace});
  }

  _getLineNumberStyle = (index) => {
    const { error } = this.props;

    if (index === error.location.line) {
      return {backgroundColor: "#ff0000", color: "#ffffff"};
    } else if (index > error.location.line) {
      return {opacity: 0};
    } else {
      return {};
    }
  }
  _getMarker(error) {
    if (error.location.column === 1) {
      return "^";
    } else if (error.location.column > 1) {
      return Array((error.location.column) - String("^").length + 1).join(" ") + "^";
    } else {
      return "";
    }
  }

  render() {
    const { error } = this.props;

    return (
      <div>
        <div className="mt-4 mb-2">
          <span className="text-danger">{error.message}</span>
          <br />
          <span className="text-danger">{error.cause}</span>
        </div>

        {this._renderCode()}
        {this._renderStackTrace()}
      </div>
    )
  }

  _renderCode() {
    const { error } = this.props;

    if (error.sourceCode === null) {
      return null;
    }

    const fileType = error.getFileType();

    return (
      <div className="mt-4 mb-2">
        <div>
          <span className="text-danger">{error.location.path}</span>
          <span>&nbsp;</span>
          <span className="text-danger">(line: {error.location.line}, column: {error.location.column})</span>
        </div>
        <SyntaxHighlighter
          language={fileType === "jade" ? "pug" : fileType}
          lineNumberStyle={this._getLineNumberStyle}
          showLineNumbers={true}
          startingLineNumber={error.sourceCode.from}
          style={style}
          wrapLines={true}
        >
          {error.sourceCode.code + this._getMarker(error)}
        </SyntaxHighlighter>
      </div>
    )
  }

  _renderStackTrace() {
    const { showStackTrace } = this.state;

    return (
      <div className="mt-4 mb-2">
        <button className="btn btn-secondary" type="button" onClick={this._toggleStackTrace}>
          {showStackTrace ? "Hide stacktrace" : "Show stacktrace"}
        </button>
        <div className="mt-2">
          {showStackTrace ? <pre>{this.props.error.stackTrace}</pre> : null }
        </div>
      </div>
    )
  }
}
