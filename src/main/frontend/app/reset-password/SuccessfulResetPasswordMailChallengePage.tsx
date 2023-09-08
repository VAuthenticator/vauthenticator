import React from "react";
import {Divider, Grid, ThemeProvider, Typography} from "@mui/material";
import theme from "../component/styles";
import Template from "../component/Template";
import {VpnKey} from "@mui/icons-material";
import ComponentInitializer from "../utils/ComponentInitializer";


const SuccessfulResetPasswordMailChallenge = () => {
    return (
        <ThemeProvider theme={theme}>
            <Template maxWidth="lg">
                <Typography variant="h3" component="h3">
                    <VpnKey fontSize="large"/> Reset Password
                </Typography>

                <Grid style={{marginTop: '10px'}}>
                    <Divider/>
                </Grid>

                <Typography variant="h3" component="h3">
                    We are sent an email on your account inbox please follow the instruction on the mail to reset your
                    password
                </Typography>
            </Template>
        </ThemeProvider>
    )
}


const page = <SuccessfulResetPasswordMailChallenge/>

ComponentInitializer(page)
