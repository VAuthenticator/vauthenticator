import React from 'react';
import ReactDOM from 'react-dom';
import {Grid, withStyles} from "@material-ui/core";
import Typography from "@material-ui/core/Typography";
import {VpnKey} from "@material-ui/icons";
import Divider from "@material-ui/core/Divider";
import vauthenticatorStyles from "../component/styles";
import Template from "../component/Template";

const Login = withStyles(vauthenticatorStyles)((props) => {
    const {classes, features} = props;

    return (
        <Template maxWidth="sm" classes={classes}>
            <Typography variant="h3" component="h3">
                <VpnKey fontSize="large"/> VAuthenticator
            </Typography>

            <Grid style={{marginTop: '10px'}}>
                <Divider/>
            </Grid>

           SignUp
        </Template>
    )
})

if (document.getElementById('app')) {
    let features = document.getElementById('features').innerHTML
    ReactDOM.render(<Login features={features} />, document.getElementById('app'));
}