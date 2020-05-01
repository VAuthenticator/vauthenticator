import React from 'react';
import {withStyles} from "@material-ui/core";
import Typography from "@material-ui/core/Typography";
import {GroupAdd} from "@material-ui/icons";
import vauthenticatorStyles from "../../component/styles";
import Template from "../../component/Template";

const ClientAppManagementPage = withStyles(vauthenticatorStyles)((props) => {
    const {classes} = props;
    console.log("OK")
    return (
        <Template maxWidth="lg" classes={classes}>

            <Typography variant="h3" component="h3">
                <GroupAdd fontSize="large"/> VAuthenticator Admin
            </Typography>

        </Template>
    );
})

export default ClientAppManagementPage