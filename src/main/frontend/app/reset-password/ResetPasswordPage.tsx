import React from 'react';
import ReactDOM from 'react-dom';
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
    metadata: string
}

const ResetPasswordPage: React.FC<ResetPasswordPageProps> = ({metadata}) => {
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
                    <VpnKey fontSize="large"/> Reset your password
                </Typography>

                <Grid style={{marginTop: '10px'}}>
                    <Divider/>
                </Grid>

                <Paper>
                    <FormInputTextField id="newPassword"
                                        label="New Password"
                                        type="Password"
                                        required={true}
                                        handler={(value) => {
                                            setPassword(value.target.value)
                                        }}
                                        value={password || ""}
                                        suffix={<Person fontSize="large"/>}/>

                    <Separator/>

                    <FormButton type="button" label="Reset passwrd"
                                onClickHandler={() => resetPassword(JSON.parse(metadata)["ticket"], password)}/>
                </Paper>
            </Template>
        </ThemeProvider>
    )
}

let metadata = getDataFromDomUtils('metadata')
let page = <ResetPasswordPage metadata={metadata}/>;

ComponentInitializer(page)