import React from "react";
import Template from "../component/Template";
import vauthenticatorStyles from "../component/styles";
import {Divider, Grid, Typography, withStyles} from "@mui/material";
import {VpnKey} from "@mui/icons-material";

const SuccessfulSignUpPage = withStyles(vauthenticatorStyles)((props) => {
    const {classes} = props;
    return (
        <Template maxWidth="lg" classes={classes}>
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
    )
})


export default SuccessfulSignUpPage