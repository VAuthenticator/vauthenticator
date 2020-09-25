import React from 'react';
import ReactDOM from 'react-dom';
import {withStyles} from "@material-ui/core";

import vauthenticatorStyles from "../component/styles";
import {HashRouter} from "react-router-dom";
import {Route, Switch} from "react-router";
import ClientAppListPage from "./clientapp/ClientAppListPage";
import ClientAppManagementPage from "./clientapp/ClientAppManagementPage";
import RolesManagementPage from "./roles/RolesManagementPage";
import HomePage from "./home/HomePage";
import AccountListPage from "./account/AccountListPage";

const VAuthenticatorAdminApp = withStyles(vauthenticatorStyles)((props) => {
    return (
        <HashRouter>
            <Switch>
                <Route exact={true} path="/"
                       render={(props) => <HomePage {...props} />}/>

                <Route exact={true} path="/client-applications/list"
                       render={(props) => <ClientAppListPage {...props} />}/>

                <Route exact={true} path="/client-applications/save"
                       render={(props) => <ClientAppManagementPage {...props} />}/>

                <Route exact={true} path="/client-applications/edit/:clientAppId"
                       render={(props) => <ClientAppManagementPage {...props} />}/>

                <Route exact={true} path="/roles"
                       render={(props) => <RolesManagementPage {...props} />}/>
                <Route exact={true} path="/accounts"
                       render={(props) => <AccountListPage {...props} />}/>
            </Switch>
        </HashRouter>)

})

if (document.getElementById('app')) {
    ReactDOM.render(<VAuthenticatorAdminApp/>, document.getElementById('app'));
}