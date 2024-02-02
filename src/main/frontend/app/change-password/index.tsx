import React from 'react';
import Template from "../component/Template";
import {Divider, Grid, Paper, ThemeProvider, Typography} from "@mui/material";
import {Fingerprint, VpnKey} from "@mui/icons-material";
import theme from "../component/styles";
import getDataFromDomUtils from "../utils/getDataFromDomUtils";
import ComponentInitializer from "../utils/ComponentInitializer";
import FormButton from "../component/FormButton";
import FormInputTextField from "../component/FormInputTextField";
import Separator from "../component/Separator";

interface ChangePasswordPageProps {
    rawI18nMessages: string
}

const ResetChangePasswordPage: React.FC<ChangePasswordPageProps> = ({rawI18nMessages}) => {
    const i18nMessages = JSON.parse(rawI18nMessages);
    return (
        <ThemeProvider theme={theme}>
            <Template maxWidth="sm">
                <Typography variant="h3" component="h3">
                    <VpnKey fontSize="large"/> {i18nMessages["pageTitleText"]}
                </Typography>

                <Grid style={{marginTop: '10px'}}>
                    <Divider/>
                </Grid>

                <Paper>
                    <form action="/change-password" method="post">
                        <FormInputTextField id="new-password"
                                            label={i18nMessages["passwordPlaceholderText"]}
                                            type="password"
                                            suffix={<Fingerprint fontSize="large"/>}/>

                        <Separator/>
                        <FormButton type="submit" label={i18nMessages["submitButtonTextReset"]}/>
                    </form>
                </Paper>
            </Template>
        </ThemeProvider>
    )
}

const rawI18nMessages = getDataFromDomUtils('i18nMessages')

ComponentInitializer(<ResetChangePasswordPage rawI18nMessages={rawI18nMessages}/>)