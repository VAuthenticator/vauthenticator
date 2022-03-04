import {Grid, withStyles} from "@material-ui/core";
import React from "react";
import Template from "../component/Template";
import Typography from "@material-ui/core/Typography";
import {VpnKey} from "@material-ui/icons";
import Divider from "@material-ui/core/Divider";
import vauthenticatorStyles from "../component/styles";

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