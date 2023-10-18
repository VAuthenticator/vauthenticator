import React from 'react';
import Template from "../component/Template";
import {Divider, Grid, Paper, ThemeProvider, Typography} from "@mui/material";
import {VpnKey} from "@mui/icons-material";
import theme from "../component/styles";
import getDataFromDomUtils from "../utils/getDataFromDomUtils";
import ComponentInitializer from "../utils/ComponentInitializer";

interface ChangePasswordPageProps {
    metadata: string
}

const ResetChangePasswordPage: React.FC<ChangePasswordPageProps> = ({metadata}) => {

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
                    <form action="/change-password" method="post">

                        <button type="submit">Submit</button>
                    </form>
                </Paper>
            </Template>
        </ThemeProvider>
    )
}

let metadata = getDataFromDomUtils('metadata')
let page = <ResetChangePasswordPage metadata={metadata}/>;

ComponentInitializer(page)