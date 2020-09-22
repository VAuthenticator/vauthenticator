import React, {useEffect} from 'react';
import {withStyles} from "@material-ui/core";
import {AssignmentInd} from "@material-ui/icons";
import vauthenticatorStyles from "../../component/styles";
import AdminTemplate from "../../component/AdminTemplate";
import StickyHeadTable from "../../component/StickyHeadTable";
import {findAllRoles} from "./RoleRepository";
import FormButton from "../../component/FormButton";

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
    const pageTitle = "Roles Management"
    const [roles, setRoles] = React.useState([])

    useEffect(() => {
        findAllRoles()
            .then(values => {
                setRoles(values)
            });
    }, []);

    return (
        <AdminTemplate maxWidth="xl" classes={classes} page={pageTitle}>

            <FormButton type="button"
                        labelPrefix={<AssignmentInd fontSize="large"/>}
                        label={"New Role"}/>

            <StickyHeadTable columns={columns} rows={roles}/>

        </AdminTemplate>
    );
})

export default RolesManagementPage