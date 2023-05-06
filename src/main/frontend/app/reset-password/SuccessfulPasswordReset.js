import React from 'react';
import ReactDOM from 'react-dom';
import {Divider, Grid, ThemeProvider, Typography} from "@mui/material";
import theme from "../component/styles";
import Template from "../component/Template";
import {VpnKey} from "@mui/icons-material";

const ResetPasswordMainPage = () => {
    return (
        <ThemeProvider theme={theme}>
            <Template maxWidth="lg">
                <Typography variant="h3" component="h3">
                    <VpnKey fontSize="large"/> Reset Password
                </Typography>

                <Grid style={{marginTop: '10px'}}>
                    <Divider/>
                </Grid>

                <Typography variant="h3" component="h2">
                    Password reset suceeded
                </Typography>
            </Template>
        </ThemeProvider>
    )
}

if (document.getElementById('app')) {
    let features = document.getElementById('features').innerHTML
    ReactDOM.render(<ResetPasswordMainPage rawFeatures={features}/>, document.getElementById('app'));
}