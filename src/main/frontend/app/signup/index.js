import React from 'react';
import {withStyles} from "@material-ui/core";
import vauthenticatorStyles from "../component/styles";
import {HashRouter} from 'react-router-dom';
import SignUpPage from "./SignUpPage";
import SuccessfulSignUpPage from "./SuccessfulSignUpPage";
import {Route, Routes} from "react-router";
import {createRoot} from "react-dom/client";

const SignUpFlow = withStyles(vauthenticatorStyles)((props) => {
    console.log("render it")
    return (
        <HashRouter>
            <Routes>
                <Route index path="/"
                       element={ <SignUpPage {...props} />}/>
                <Route exact={true} path="/succeeded"
                       element={ <SuccessfulSignUpPage {...props} />}/>
            </Routes>
        </HashRouter>)

})

if (document.getElementById('app')) {
    let features = document.getElementById('features').innerHTML
    const container = document.getElementById('app');
    const root = createRoot(container);
    root.render(<SignUpFlow features={features}/>);
}