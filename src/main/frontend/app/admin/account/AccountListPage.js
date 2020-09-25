import React from 'react';
import {withStyles} from "@material-ui/core";
import vauthenticatorStyles from "../../component/styles";
import AdminTemplate from "../../component/AdminTemplate";

const AccountListPage= withStyles(vauthenticatorStyles)((props) => {
    const {classes} = props;
    const pageTitle = "Accounts Management"
    return (
        <AdminTemplate maxWidth="xl" classes={classes} page={pageTitle}>

        </AdminTemplate>
    )
})

export default AccountListPage