import ReactDOM from "react-dom";
import React from "react";
import DefaultGenericErrorPage from "./DefaultGenericErrorPage";

if (document.getElementById('app')) {
    let errors = document.getElementById('errors').innerHTML
    ReactDOM.render(<DefaultGenericErrorPage messages={errors}/>, document.getElementById('app'));
}