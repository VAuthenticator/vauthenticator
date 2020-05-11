import React, {useEffect, useState} from 'react';
import {withStyles} from "@material-ui/core";
import Typography from "@material-ui/core/Typography";
import {GroupAdd} from "@material-ui/icons";
import vauthenticatorStyles from "../../component/styles";
import {useHistory, useParams} from "react-router";
import {findClientApplicationFor, saveClientApplicationFor} from "./ClientAppRepository";
import FormInputTextField from "../../component/FormInputTextField";
import AdminTemplate from "../../component/AdminTemplate";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import CardHeader from "@material-ui/core/CardHeader";
import Separator from "../../component/Separator";
import FormButton from "../../component/FormButton";
import Tab from "@material-ui/core/Tab";
import Tabs from "@material-ui/core/Tabs";
import TabPanel from "../../component/TabPanel";

function a11yProps(index) {
    return {
        id: `vertical-tab-${index}`,
        'aria-controls': `vertical-tabpanel-${index}`,
    };
}


const ClientAppManagementPage = withStyles(vauthenticatorStyles)((props) => {
    const {classes} = props;
    let {clientAppId} = useParams();
    const storePassword = !clientAppId
    const history = useHistory();

    const [clientApplicationId, setClientApplicationId] = useState(clientAppId)
    const [clientAppName, setClientAppName] = useState("")
    const [secret, setSecret] = useState("*********")
    const [scopes, setScopes] = useState([])
    const [authorizedGrantTypes, setAuthorizedGrantTypes] = useState([])
    const [webServerRedirectUri, setWebServerRedirectUri] = useState("")
    const [authorities, setAuthorities] = useState([])
    const [accessTokenValidity, setAccessTokenValidity] = useState("")
    const [refreshTokenValidity, setRefreshTokenValidity] = useState("")
    const [postLogoutRedirectUri, setPostLogoutRedirectUri] = useState("")
    const [logoutUri, setLogoutUri] = useState("")
    const [federation, setFederation] = useState("")

    const saveClientApp = () => {
        let clientApplication = {
            "clientAppName": clientAppName,
            "secret": secret,
            "scopes": scopes,
            "authorizedGrantTypes": authorizedGrantTypes,
            "webServerRedirectUri": webServerRedirectUri,
            "authorities": authorities,
            "accessTokenValidity": accessTokenValidity,
            "refreshTokenValidity": refreshTokenValidity,
            "postLogoutRedirectUri": postLogoutRedirectUri,
            "logoutUri": logoutUri,
            "federation": federation,
            "storePassword": storePassword
        }
        saveClientApplicationFor(clientApplicationId, clientApplication)
            .then(response => {
                if (response.status === 204) {
                    history.replace("/client-applications/list");
                }
            })
    }

    useEffect(() => {
        findClientApplicationFor(clientApplicationId)
            .then(value => {
                console.log(value)
                setClientAppName(value.clientAppName)
                setSecret(value.secret)
                setScopes(value.scopes)
                setAuthorizedGrantTypes(value.authorizedGrantTypes)
                setWebServerRedirectUri(value.webServerRedirectUri)
                setAuthorities(value.authorities)
                setAccessTokenValidity(value.accessTokenValidity)
                setRefreshTokenValidity(value.refreshTokenValidity)
                setPostLogoutRedirectUri(value.postLogoutRedirectUri)
                setLogoutUri(value.logoutUri)
                setFederation(value.federation)
            })
    }, {})
    const [value, setValue] = React.useState('0');
    const handleChange = (event, newValue) => {
        setValue(newValue);
    };

    return (
        <AdminTemplate maxWidth="xl" classes={classes}>

            <Typography variant="h3" component="h3">
                <GroupAdd fontSize="large"/> Client Application: {clientApplicationId}
            </Typography>

            <div className={classes.tabs} >
                <Tabs value={value}
                      orientation="vertical"
                      onChange={handleChange}
                      aria-label="wrapped label tabs example">
                    <Tab
                        value="0"
                        label="1) Client Application Credentials Section"
                        wrapped
                        {...a11yProps('0')}
                    />
                    <Tab value="1"
                         label="2) Client Application Permission Definition Section"
                         {...a11yProps('1')} />

                    <Tab value="2"
                         label="3) Client Application Uri Section"
                         {...a11yProps('2')} />
                </Tabs>
                <TabPanel value={value} index={'0'}>
                    <Card className={classes.card} >
                        <CardHeader title="Client Application base definition"
                                    className={classes.title}
                                    color="textSecondary">
                        </CardHeader>
                        <CardContent>
                            <FormInputTextField id="clientAppId"
                                                label="Client Application Id"
                                                required={true}
                                                handler={(value) => {
                                                    setClientApplicationId(value.target.value)
                                                }}
                                                value={clientApplicationId || ""}/>

                            <FormInputTextField id="secret"
                                                label="Password"
                                                required={true}
                                                type="Password"
                                                disabled={clientAppId}
                                                handler={(value) => {
                                                    setSecret(value.target.value)
                                                }}
                                                value={secret}/>

                            <FormInputTextField id="clientAppName"
                                                label="Client Application Displayed Name"
                                                required={true}
                                                handler={(value) => {
                                                    setClientAppName(value.target.value)
                                                }}
                                                value={clientAppName}/>

                            <FormInputTextField id="federation"
                                                label="Federation"
                                                handler={(value) => {
                                                    setFederation(value.target.value)
                                                }}
                                                value={federation}/>
                        </CardContent>
                    </Card>
                    <Separator />
                    <FormButton lable="Next Tab" onClickHandler={() => setValue('1')}/>
                </TabPanel>

                <TabPanel value={value} index={'1'}>
                    <Card className={classes.card}>
                        <CardContent>
                            <CardHeader title="Client Application permission specification"
                                        className={classes.title}
                                        color="textSecondary">
                            </CardHeader>

                            <FormInputTextField id="scopes"
                                                label="Scopes"
                                                required={true}
                                                handler={(value) => {
                                                    setScopes(value.target.value.split(","))
                                                }}
                                                value={scopes}/>

                            <FormInputTextField id="authorizedGrantTypes"
                                                label="Authorized Grant Types"
                                                required={true}
                                                handler={(value) => {
                                                    setAuthorizedGrantTypes(value.target.value.split(","))
                                                }}
                                                value={authorizedGrantTypes}/>


                            <FormInputTextField id="authorities"
                                                label="Authorities"
                                                required={true}
                                                handler={(value) => {
                                                    setAuthorities(value.target.value.split(","))
                                                }}
                                                value={authorities}/>

                            <FormInputTextField id="accessTokenValidity"
                                                label="Access Token Validity"
                                                required={true}
                                                handler={(value) => {
                                                    setAccessTokenValidity(value.target.value)
                                                }}
                                                value={accessTokenValidity}/>

                            <FormInputTextField id="refreshTokenValidity"
                                                label="Refresh Token Validity"
                                                required={true}
                                                handler={(value) => {
                                                    setRefreshTokenValidity(value.target.value)
                                                }}
                                                value={refreshTokenValidity}/>
                        </CardContent>
                    </Card>
                    <Separator/>

                    <div style={{flex: "0 0 auto",
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "flex-end"
                    }}>
                        <FormButton lable="Previous Tab" onClickHandler={() => setValue('0')}/>
                        <FormButton lable="Next Tab" onClickHandler={() => setValue('2')}/>
                    </div>
                </TabPanel>
                <TabPanel value={value} index={'2'}>
                    <Card className={classes.card}>
                        <CardContent>
                            <CardHeader title="Client Application urls definitions"
                                        className={classes.title}
                                        color="textSecondary">
                            </CardHeader>

                            <FormInputTextField id="webServerRedirectUri"
                                                label="Web Server Redirect Uri"
                                                required={true}
                                                handler={(value) => {
                                                    setWebServerRedirectUri(value.target.value)
                                                }}
                                                value={webServerRedirectUri}/>

                            <FormInputTextField id="postLogoutRedirectUri"
                                                label="Post Logout Redirect Uri"
                                                required={true}
                                                handler={(value) => {
                                                    setPostLogoutRedirectUri(value.target.value)
                                                }}
                                                value={postLogoutRedirectUri}/>

                            <FormInputTextField id="logoutUri"
                                                label="Logout Uri"
                                                required={true}
                                                handler={(value) => {
                                                    setLogoutUri(value.target.value)
                                                }}
                                                value={logoutUri}/>
                        </CardContent>
                    </Card>
                    <Separator/>
                    <div style={{flex: "0 0 auto",
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "flex-end"
                    }}>
                        <FormButton lable="Previous Tab" onClickHandler={() => setValue('1')}/>
                        <FormButton lable="Save Client Application" onClickHandler={saveClientApp}/>
                    </div>

                </TabPanel>


            </div>
        </AdminTemplate>
    );
})

export default ClientAppManagementPage