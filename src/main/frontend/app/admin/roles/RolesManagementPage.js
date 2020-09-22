import React, {useEffect} from 'react';
import {withStyles} from "@material-ui/core";
import {AssignmentInd, Delete} from "@material-ui/icons";
import vauthenticatorStyles from "../../component/styles";
import AdminTemplate from "../../component/AdminTemplate";
import StickyHeadTable from "../../component/StickyHeadTable";
import {deleteRoleFor, findAllRoles} from "./RoleRepository";
import FormButton from "../../component/FormButton";
import EditIcon from "@material-ui/icons/Edit";
import ConfirmationDialog from "../../component/ConfirmationDialog";

const columns = [
    {id: 'name', label: 'Role', minWidth: 170},
    {id: 'description', label: 'Description', minWidth: 170},
    {id: 'edit', label: 'Edit Role', minWidth: 170},
    {id: 'delete', label: 'Delete Role', minWidth: 170}
];

const RolesManagementPage = withStyles(vauthenticatorStyles)((props) => {
    const {classes} = props;
    const pageTitle = "Roles Management"
    const [roles, setRoles] = React.useState([])
    const [open, setOpen] = React.useState(false)
    const [selectedRole, setSelectedRole] = React.useState("")

    const getEditLinkFor = () => {
        return <EditIcon/>
    }

    const handleClose = () => {
        setOpen(false);
    };

    const getDeleteLinkFor = (role) => {
        return <Delete onClick={() => {
            setSelectedRole(role)
            setOpen(true);
        }}/>;
    }

    const deleteRole = () => {
        deleteRoleFor(selectedRole)
            .then(response => {
                if (response.status === 204) {
                    setOpen(false);
                    fetchAllRoles()
                }
            })
    }

    const fetchAllRoles = () => {
        findAllRoles()
            .then(values => {
                let rows = values.map(value => {
                    return {
                        name: value.name,
                        description: value.description,
                        edit: getEditLinkFor(value["name"]),
                        delete: getDeleteLinkFor(value["name"])
                    }
                })

                setRoles(rows)
            });
    }

    useEffect(() => {
        fetchAllRoles()
    }, []);

    return (
        <AdminTemplate maxWidth="xl" classes={classes} page={pageTitle}>
            <ConfirmationDialog maxWidth="md"
                                open={open}
                                onExecute={deleteRole}
                                onClose={handleClose}
                                message="Are you sure delete the selected role"
                                title="Role delete"/>

            <FormButton type="button"
                        labelPrefix={<AssignmentInd fontSize="large"/>}
                        label={"New Role"}/>

            <StickyHeadTable columns={columns} rows={roles}/>

        </AdminTemplate>
    );
})

export default RolesManagementPage