import {Box, Divider, Grid, ThemeProvider, Typography} from "@mui/material";
import theme from "../component/styles";
import Template from "../component/Template";
import {Person, VpnKey} from "@mui/icons-material";
import FormInputTextField from "../component/FormInputTextField";
import Separator from "../component/Separator";
import FormButton from "../component/FormButton";
import React from "react";
import ErrorBanner from "../component/ErrorBanner";
import getDataFromDomUtils from "../utils/getDataFromDomUtils";
import ComponentInitializer from "../utils/ComponentInitializer";

interface MfaChallengePageProps {
    rawErrors: string
}

const MfaChallengePage: React.FC<MfaChallengePageProps> = ({rawErrors}) => {
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

let errors = getDataFromDomUtils('errors')
let page = <MfaChallengePage rawErrors={errors}/>;
ComponentInitializer(page)