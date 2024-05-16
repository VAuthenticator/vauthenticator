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
import Modal from "../component/Modal";

interface MfaChallengePageProps {
    rawErrors: string
    rawI18nMessages: string
}

const MfaChallengePage: React.FC<MfaChallengePageProps> = ({rawErrors, rawI18nMessages}) => {
    const sendMfaCode = () => {
        fetch("/api/mfa/challenge", {
            method: 'PUT', // *GET, POST, PUT, DELETE, etc.
            credentials: 'same-origin', // include, *same-origin, omit
        })
    }

    const errorMessage = JSON.parse(rawErrors)["mfa-challenge"];
    const i18nMessages = JSON.parse(rawI18nMessages)

    const errorsBanner = <ErrorBanner errorMessage={errorMessage}/>
    const [openChooseMFAModal, setOpenChooseMFAModal] = React.useState(false)

    sendMfaCode()

    return (
        <ThemeProvider theme={theme}>

            <Modal maxWidth="md"
                   open={openChooseMFAModal}
                   onExecute={deleteRole}
                   onClose={handleCloseConfirmationDialog}
                   message="Are you sure delete the selected role"
                   title="Choose your preferred MFA method"/>

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

                        <FormButton type="submit" label={i18nMessages["submitButtonText"]} buttonColor={"success"}/>

                        <Separator/>

                        <Grid container sm={12}>
                            <Grid item sm={4}>
                                <FormButton type="button" label={i18nMessages["sendAgainButtonText"]}
                                            onClickHandler={() => sendMfaCode()}/>
                            </Grid>
                            <Grid item sm={4}> </Grid>
                            <Grid item sm={4}>
                                <FormButton type="button" label={i18nMessages["changeMfaMethodButtonText"]} direction={"rtl"}
                                            onClickHandler={() => sendMfaCode()}/>
                            </Grid>
                        </Grid>
                    </Box>
                </form>}
            </Template>
        </ThemeProvider>

    )
}

const errors = getDataFromDomUtils('errors')
const i18nMessages = getDataFromDomUtils('i18nMessages')

ComponentInitializer(<MfaChallengePage rawErrors={errors} rawI18nMessages={i18nMessages}/>)