import React from 'react';
import {Divider, Grid, ThemeProvider, Typography} from "@mui/material";
import theme from "../component/styles";
import Template from "../component/Template";
import {Mail} from "@mui/icons-material";
import getDataFromDomUtils from "../utils/getDataFromDomUtils";
import ComponentInitializer from "../utils/ComponentInitializer";

interface SuccessfulMailVerifyPageProps {
    rawFeatures: string
}

const SuccessfulMailVerifyPage: React.FC<SuccessfulMailVerifyPageProps> = ({rawFeatures}) => {
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

let features = getDataFromDomUtils('features')
let page = <SuccessfulMailVerifyPage rawFeatures={features}/>;

ComponentInitializer(page)