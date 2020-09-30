import React from 'react';
import {saveRoleFor} from "./RoleRepository";
import FormButton from "../../component/FormButton";
import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import Separator from "../../component/Separator";
import DialogActions from "@material-ui/core/DialogActions";
import FormInputTextField from "../../component/FormInputTextField";

export default function RoleDialog({onClose, open, title, role, setRole, isRoleReadOnly}) {

    return (
        <Dialog onClose={onClose} aria-labelledby="simple-dialog-title" open={open} maxWidth="md">
            <DialogTitle id="simple-dialog-title">{title}</DialogTitle>
            <DialogContent>
                <FormInputTextField id="name"
                                    label="Role Name"
                                    type="text"
                                    disabled={isRoleReadOnly}
                                    value={role.name}
                                    handler={(value) => {
                                        setRole({name: value.target.value, description: role.description})
                                    }}/>

                <FormInputTextField id="description"
                                    label="Role Description" t
                                    type="text"
                                    value={role.description}
                                    handler={(value) => {
                                        setRole({name: role.name, description: value.target.value})
                                    }}/>

                <Separator/>

                <DialogActions>
                    <FormButton label="Save" onClickHandler={() => {
                        saveRoleFor(role)
                            .then(response => {
                                if (response.status === 204) {
                                    onClose(true)
                                }
                            })
                    }}/>
                    <FormButton label="Cancel" onClickHandler={onClose}/>
                </DialogActions>
            </DialogContent>
        </Dialog>
    );
}