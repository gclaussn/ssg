import React from "react";
import ReactDOM from "react-dom";
import { BrowserRouter as Router } from "react-router-dom";

import App from "App";
import AppNav from "AppNav";

import "bootstrap/dist/css/bootstrap.css";
import "@fortawesome/fontawesome-free/css/all.min.css";
import "./index.css";

ReactDOM.render(
  <Router basename="/app">
    <AppNav />
    <App />
  </Router>
, document.getElementById("app"));
