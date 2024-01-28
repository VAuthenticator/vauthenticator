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
    rawI18nMessages: string
}

const MfaChallengePage: React.FC<MfaChallengePageProps> = ({rawErrors,rawI18nMessages}) => {
    let sendAgainMfaCode = () => {
        fetch("/mfa-challenge/send", {
            method: 'PUT', // *GET, POST, PUT, DELETE, etc.
            credentials: 'same-origin', // include, *same-origin, omit
        });
    }

    let errorMessage = JSON.parse(rawErrors)["mfa-challenge"];
    let i18nMessages = JSON.parse(rawI18nMessages)

    const errorsBanner = <ErrorBanner errorMessage={errorMessage}/>

    return (
        <ThemeProvider theme={theme}>

            <Template maxWidth="sm">
                <Typography variant="h3" component="h3">
                    <VpnKey fontSize="large"/> {i18nMessages["pageTitleText"]}
                </Typography>

                <Grid style={{marginTop: '10px'}}>
                    <Divider/>
                </Grid>
                {errorMessage ? errorsBanner : ""}

                {<form action="mfa-challenge" method="post">
                    <Box>
                        <FormInputTextField id="mfa-code"
                                            label={i18nMessages["mfaPlaceholderText"]}
                                            type="text"
                                            suffix={<Person fontSize="large"/>}/>

                        <Separator/>

                        <FormButton type="submit" label={i18nMessages["submitButtonText"]}/>

                        <FormButton type="button" label={i18nMessages["sendAgainButtonText"]} onClickHandler={() => sendAgainMfaCode()}/>
                    </Box>
                </form>}
            </Template>
        </ThemeProvider>

    )
}

let errors = getDataFromDomUtils('errors')
let i18nMessages = getDataFromDomUtils('i18nMessages')

let page = <MfaChallengePage rawErrors={errors} rawI18nMessages={i18nMessages}/>;

ComponentInitializer(page)