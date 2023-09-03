import {Box, Divider, Grid, ThemeProvider, Typography} from "@mui/material";
import theme from "../component/styles";
import Template from "../component/Template";
import {Person, VpnKey} from "@mui/icons-material";
import FormInputTextField from "../component/FormInputTextField";
import Separator from "../component/Separator";
import FormButton from "../component/FormButton";
import React from "react";
import ReactDOM from "react-dom";
import ErrorBanner from "../component/ErrorBanner";

const MfaChallengePage = ({rawErrors}) => {
    let sendAgainMfaCode = () => {
        fetch("/mfa-challenge/send", {
            method: 'PUT', // *GET, POST, PUT, DELETE, etc.
            credentials: 'same-origin', // include, *same-origin, omit
        });
    }

    let errorMessage = JSON.parse(rawErrors)["mfa-challenge"];
    const errorsBanner = <ErrorBanner errorMessage={errorMessage}/>

    return (
        <ThemeProvider theme={theme}>

            <Template maxWidth="sm">
                <Typography variant="h3" component="h3">
                    <VpnKey fontSize="large"/> VAuthenticator MFA module
                </Typography>

                <Grid style={{marginTop: '10px'}}>
                    <Divider/>
                </Grid>
                {errorMessage ? errorsBanner : ""}

                {<form action="mfa-challenge" method="post">
                    <Box>
                        <FormInputTextField id="mfa-code"
                                            label="mfa-code"
                                            type="text"
                                            suffix={<Person fontSize="large"/>}/>

                        <Separator/>

                        <FormButton type="submit" label="Login"/>

                        <FormButton type="button" label="Send again code" onClickHandler={() => sendAgainMfaCode()}/>
                    </Box>
                </form>}
            </Template>
        </ThemeProvider>

    )
}
let errors = document.getElementById('errors').innerHTML
let features = document.getElementById('features').innerHTML
ReactDOM.render(<MfaChallengePage rawFeatures={features} rawErrors={errors}/>, document.getElementById('app'));