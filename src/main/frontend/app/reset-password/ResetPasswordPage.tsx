import React from 'react';
import Template from "../component/Template";
import FormInputTextField from "../component/FormInputTextField";
import Separator from "../component/Separator";
import FormButton from "../component/FormButton";
import {Divider, Grid, Paper, ThemeProvider, Typography} from "@mui/material";
import {Person, VpnKey} from "@mui/icons-material";
import theme from "../component/styles";
import getDataFromDomUtils from "../utils/getDataFromDomUtils";
import ComponentInitializer from "../utils/ComponentInitializer";

interface ResetPasswordPageProps {
    rawMetadata: string
    rawI18nMessages: string
}

const ResetPasswordPage: React.FC<ResetPasswordPageProps> = ({rawMetadata, rawI18nMessages}) => {
    const i18nMessages = JSON.parse(rawI18nMessages);
    const metadata = JSON.parse(rawMetadata);

    const [password, setPassword] = React.useState("")
    const resetPassword = (ticket: string, password: string) => {
        return fetch(`/api/reset-password/${ticket}`, {
            method: "PUT",
            credentials: 'same-origin',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                newPassword: password
            })
        }).then(r => {
            console.log("send reset password")
            if (r.status === 204) {
                window.document.location.href = "/reset-password/successful-password-reset"
            }
        })

    }
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
                    <FormInputTextField id="newPassword"
                                        label={i18nMessages["passwordPlaceholderText"]}
                                        type="password"
                                        required={true}
                                        handler={(value) => {
                                            setPassword(value.target.value)
                                        }}
                                        value={password || ""}
                                        suffix={<Person fontSize="large"/>}/>

                    <Separator/>

                    <FormButton type="button" label={i18nMessages["submitButtonTextReset"]}
                                onClickHandler={() => resetPassword(metadata["ticket"], password)}/>
                </Paper>
            </Template>
        </ThemeProvider>
    )
}

const metadata = getDataFromDomUtils('metadata')
const i18nMessages = getDataFromDomUtils('i18nMessages')

ComponentInitializer(<ResetPasswordPage rawMetadata={metadata} rawI18nMessages={i18nMessages}/>)