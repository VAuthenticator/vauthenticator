import React from 'react';
import {withStyles} from "@material-ui/core";
import Typography from "@material-ui/core/Typography";
import {AssignmentInd} from "@material-ui/icons";
import vauthenticatorStyles from "../../component/styles";
import AdminTemplate from "../../component/AdminTemplate";


function allProps(index) {
    return {
        id: `vertical-tab-${index}`,
        'aria-controls': `vertical-tabpanel-${index}`,
    };
}

const RolesManagementPage = withStyles(vauthenticatorStyles)((props) => {
    const {classes} = props;
    const pageTitle = "User Roles Management"
    return (
        <AdminTemplate maxWidth="xl" classes={classes} page={pageTitle}>

            <Typography variant="h3" component="h3">
                <AssignmentInd fontSize="large"/> User Roles Management
            </Typography>

            <div className={classes.tabs}>
                ... content
            </div>
        </AdminTemplate>
    );
})

export default RolesManagementPage