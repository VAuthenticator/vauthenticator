import React from "react";
import {Box, Divider, Grid, ThemeProvider, Typography} from "@mui/material";
import theme from "../component/styles";
import Template from "../component/Template";
import {Person, VpnKey} from "@mui/icons-material";
import FormInputTextField from "../component/FormInputTextField";
import Separator from "../component/Separator";
import FormButton from "../component/FormButton";
import ComponentInitializer from "../utils/ComponentInitializer";


const ResetPasswordChallengeSender = () => {
    const [email, setEmail] = React.useState("")

    const sentResetPasswordChallenge = (email: string) => {
        return fetch(`/api/reset-password-challenge`, {
            method: "PUT",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({"mail": email}),
            credentials: 'same-origin'
        }).then(r => {
            if (r.status === 204) {
                window.location.href = "/reset-password/successful-reset-password-mail-challenge";
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

                <Box>
                    <FormInputTextField id="email"
                                        label="Email"

                                        required={true}
                                        handler={(value) => {
                                            setEmail(value.target.value)
                                        }}
                                        value={email || ""}
                                        suffix={<Person fontSize="large"/>}/>

                    <Separator/>

                    <FormButton type="button" label="Reset passwrd" onClickHandler={() => {
                        sentResetPasswordChallenge(email)
                    }}/>

                </Box>

            </Template>
        </ThemeProvider>
    )
}

ComponentInitializer(<ResetPasswordChallengeSender/>)