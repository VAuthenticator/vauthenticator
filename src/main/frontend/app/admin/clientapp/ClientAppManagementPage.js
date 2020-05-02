import React, {useEffect, useState} from 'react';
import {withStyles} from "@material-ui/core";
import Typography from "@material-ui/core/Typography";
import {GroupAdd} from "@material-ui/icons";
import vauthenticatorStyles from "../../component/styles";
import {useParams} from "react-router";
import {findClientApplicationFor} from "./ClientAppRepository";
import FormInputTextField from "../../component/FormInputTextField";
import AdminTemplate from "../../component/AdminTemplate";

const ClientAppManagementPage = withStyles(vauthenticatorStyles)((props) => {
    const {classes} = props;
    let {clientAppId} = useParams();

    let emptyClientApplication = {
        clientAppName: "",
        secret: "*********",
        setSecret: false,
        scopes: [],
        authorizedGrantTypes: [],
        webServerRedirectUri: "",
        authorities: [],
        accessTokenValidity: "",
        refreshTokenValidity: "",
        postLogoutRedirectUri: "",
        logoutUri: "",
        federation: ""
    };
    const [clientApplication, setClientApplication] = useState(emptyClientApplication)

    useEffect(() => {
        findClientApplicationFor(clientAppId)
            .then(value => {
                console.log(value)
                setClientApplication(value || emptyClientApplication)
            })
    }, {})

    return (
        <AdminTemplate maxWidth="xl" classes={classes}>

            <Typography variant="h3" component="h3">
                <GroupAdd fontSize="large"/> VAuthenticator Admin {clientAppId}
            </Typography>

            <div className={classes.margin}>
                <FormInputTextField id="clientAppName"
                                    label="Client Application Displayed Name"
                                    required={true}
                                    value={clientApplication.clientAppName}/>

                <FormInputTextField id="clientAppId"
                                    label="Client Application Id"
                                    required={true}
                                    value={clientAppId || ""}/>

                <FormInputTextField id="secret"
                                    label="Password"
                                    required={true}
                                    type="Password"
                                    value={clientApplication.secret}/>

                <FormInputTextField id="scopes"
                                    label="Scopes"
                                    required={true}
                                    value={clientApplication.scopes}/>

                <FormInputTextField id="authorizedGrantTypes"
                                    label="Authorized Grant Types"
                                    required={true}
                                    value={clientApplication.authorizedGrantTypes}/>

                <FormInputTextField id="webServerRedirectUri"
                                    label="Web Server Redirect Uri"
                                    required={true}
                                    value={clientApplication.webServerRedirectUri}/>

                <FormInputTextField id="authorities"
                                    label="Authorities"
                                    required={true}
                                    value={clientApplication.authorities}/>

                <FormInputTextField id="accessTokenValidity"
                                    label="Access Token Validity"
                                    required={true}
                                    value={clientApplication.accessTokenValidity}/>

                <FormInputTextField id="refreshTokenValidity"
                                    label="Refresh Token Validity"
                                    required={true}
                                    value={clientApplication.refreshTokenValidity}/>

                <FormInputTextField id="postLogoutRedirectUri"
                                    label="Post Logout Redirect Uri"
                                    required={true}
                                    value={clientApplication.postLogoutRedirectUri}/>

                <FormInputTextField id="logoutUri"
                                    label="Logout Uri"
                                    required={true}
                                    value={clientApplication.logoutUri}/>

                <FormInputTextField id="federation"
                                    label="Federation"
                                    value={clientApplication.federation}/>
            </div>
        </AdminTemplate>
    );
})

export default ClientAppManagementPage