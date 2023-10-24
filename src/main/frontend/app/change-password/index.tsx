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
    metadata: string
}

const ResetChangePasswordPage: React.FC<ChangePasswordPageProps> = ({metadata}) => {

    return (
        <ThemeProvider theme={theme}>
            <Template maxWidth="sm">
                <Typography variant="h3" component="h3">
                    <VpnKey fontSize="large"/> Please change your password
                </Typography>

                <Grid style={{marginTop: '10px'}}>
                    <Divider/>
                </Grid>

                <Paper>
                    <form action="/change-password" method="post">
                        <FormInputTextField id="new-password"
                                            label="New Password"
                                            type="password"
                                            suffix={<Fingerprint fontSize="large"/>}/>

                        <Separator/>
                        <FormButton type="submit" label="Change Password"/>
                    </form>
                </Paper>
            </Template>
        </ThemeProvider>
    )
}

let metadata = getDataFromDomUtils('metadata')
let page = <ResetChangePasswordPage metadata={metadata}/>;

ComponentInitializer(page)