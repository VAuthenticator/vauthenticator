import React from "react";
import Template from "../component/Template";
import {Divider, Grid, ThemeProvider, Typography} from "@mui/material";
import {VpnKey} from "@mui/icons-material";
import theme from "../component/styles";
import {createRoot} from "react-dom/client";

const SuccessfulSignUpPage = () => {
    return (
        <ThemeProvider theme={theme}>
            <Template maxWidth="lg">
                <Typography variant="h3" component="h3">
                    <VpnKey fontSize="large"/> Sign Up on VAuthenticator
                </Typography>

                <Grid style={{marginTop: '10px'}}>
                    <Divider/>
                </Grid>

                <Typography variant="h3" component="h3">
                    Sign Up on VAuthenticator succeeded
                </Typography>
            </Template>
        </ThemeProvider>
    )
}


if (document.getElementById('app')) {
    let features = document.getElementById('features').innerHTML
    const container = document.getElementById('app');
    const root = createRoot(container);
    root.render(<SuccessfulSignUpPage features={features}/>);
}