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
import RolesDialog from "./RolesDialog";

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
    const [openConfirmationDeleteRoleDialog, setOpenConfirmationDeleteRoleDialog] = React.useState(false)
    const [openRolesManagementDialog, setOpenRolesManagementDialog] = React.useState(false)
    const [selectedRole, setSelectedRole] = React.useState("")
    const [role, setRole] = React.useState({name: "", description: ""})

    const getEditLinkFor = (role) => {
        return <EditIcon onClick={() => {
            setRole(role)
            setOpenRolesManagementDialog(true);
        }}/>;
    }

    const handleCloseConfirmationDialog = (refresh) => {
        setOpenConfirmationDeleteRoleDialog(false);
        if (refresh) {
            fetchAllRoles();
        }
    };

    const handleCloseRolesDialog = (refresh) => {
        setOpenRolesManagementDialog(false);
        if (refresh) {
            fetchAllRoles()
        }
    };

    const getDeleteLinkFor = (roleName) => {
        return <Delete onClick={() => {
            setSelectedRole(roleName)
            setOpenConfirmationDeleteRoleDialog(true);
        }}/>;
    }

    const deleteRole = () => {
        deleteRoleFor(selectedRole)
            .then(response => {
                if (response.status === 204) {
                    handleCloseConfirmationDialog(true)
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
                        edit: getEditLinkFor({name: value["name"], description: value["description"]}),
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
                                open={openConfirmationDeleteRoleDialog}
                                onExecute={deleteRole}
                                onClose={handleCloseConfirmationDialog}
                                message="Are you sure delete the selected role"
                                title="Role delete"/>

            <RolesDialog open={openRolesManagementDialog}
                         selectedRole={role.name}
                         selectedDescription={role.description}
                         onClose={handleCloseRolesDialog}
                         title="Role management"/>

            <FormButton type="button"
                        onClickHandler={() => {
                            setRole({name: "", description: ""})
                            setOpenRolesManagementDialog(true);
                        }}
                        labelPrefix={<AssignmentInd fontSize="large"/>}
                        label={"New Role"}/>

            <StickyHeadTable columns={columns} rows={roles}/>

        </AdminTemplate>
    );
})

export default RolesManagementPage