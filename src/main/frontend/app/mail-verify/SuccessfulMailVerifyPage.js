import React from 'react';
import ReactDOM from 'react-dom';
import {Divider, Grid, ThemeProvider, Typography} from "@mui/material";
import theme from "../component/styles";
import Template from "../component/Template";
import {Mail} from "@mui/icons-material";

const SuccessfulMailVerifyPage = () => {
    return (
        <ThemeProvider theme={theme}>
            <Template maxWidth="lg">
                <Typography variant="h3" component="h3">
                    <Mail fontSize="large"/> Confirmation of your email verification
                </Typography>

                <Grid style={{marginTop: '10px'}}>
                    <Divider/>
                </Grid>

                <Typography variant="h3" component="h2">
                    Your email has been successful verified.
                </Typography>
            </Template>
        </ThemeProvider>
    )
}

if (document.getElementById('SuccessfulMailVerifyPage')) {
    let features = document.getElementById('features').innerHTML
    ReactDOM.render(<SuccessfulMailVerifyPage rawFeatures={features}/>, document.getElementById('app'));
}