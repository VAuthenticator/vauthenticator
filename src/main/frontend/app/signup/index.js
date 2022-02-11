import React from 'react';
import ReactDOM from 'react-dom';
import {withStyles} from "@material-ui/core";
import vauthenticatorStyles from "../component/styles";
import {HashRouter} from "react-router-dom";
import {Route, Switch} from "react-router";
import SignUpPage from "./SignUpPage";

const SignUpFlow = withStyles(vauthenticatorStyles)((props) => {
    return (
        <HashRouter>
            <Switch>
                <Route exact={true} path="/"
                       render={(props) => <SignUpPage {...props} />}/>
            </Switch>
        </HashRouter>)

})

if (document.getElementById('app')) {
    let features = document.getElementById('features').innerHTML
    ReactDOM.render(<SignUpFlow features={features}/>, document.getElementById('app'));
}