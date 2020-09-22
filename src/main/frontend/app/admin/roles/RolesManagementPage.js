import React, {useEffect} from 'react';
import {withStyles} from "@material-ui/core";
import Typography from "@material-ui/core/Typography";
import {AssignmentInd} from "@material-ui/icons";
import vauthenticatorStyles from "../../component/styles";
import AdminTemplate from "../../component/AdminTemplate";
import StickyHeadTable from "../../component/StickyHeadTable";
import {findAllRoles} from "./RoleRepository";


function allProps(index) {
    return {
        id: `vertical-tab-${index}`,
        'aria-controls': `vertical-tabpanel-${index}`,
    };
}

const columns = [
    {id: 'name', label: 'Role', minWidth: 170},
    {id: 'description', label: 'Description', minWidth: 170}
];

const RolesManagementPage = withStyles(vauthenticatorStyles)((props) => {
    const {classes} = props;
    const pageTitle = "User Roles Management"
    const [roles, setRoles] = React.useState([])

    useEffect(() => {
        findAllRoles()
            .then(values => {
                setRoles(values)
            });
    }, []);

    return (
        <AdminTemplate maxWidth="xl" classes={classes} page={pageTitle}>

            <Typography variant="h3" component="h3">
                <AssignmentInd fontSize="large"/> User Roles Management
            </Typography>

            <StickyHeadTable columns={columns} rows={roles}/>

        </AdminTemplate>
    );
})

export default RolesManagementPage