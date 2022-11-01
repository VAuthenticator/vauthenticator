import React from 'react';
import ReactDOM from 'react-dom';
import vauthenticatorStyles from "../component/styles";
import {withStyles} from "@mui/material";

const ResetPasswordMainPage = withStyles(vauthenticatorStyles)((props) => {
    return (<div>It Works!!!</div>)
})


if (document.getElementById('app')) {
    let features = document.getElementById('features').innerHTML
    ReactDOM.render(<ResetPasswordMainPage rawFeatures={features}/>, document.getElementById('app'));
}