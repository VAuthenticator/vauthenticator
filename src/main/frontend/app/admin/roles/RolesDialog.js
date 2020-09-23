import React, {useState} from 'react';
import {saveRoleFor} from "./RoleRepository";
import FormButton from "../../component/FormButton";
import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import Separator from "../../component/Separator";
import DialogActions from "@material-ui/core/DialogActions";
import FormInputTextField from "../../component/FormInputTextField";

export default function RoleDialog({onClose, open, title, selectedRole}) {
    let [role, setRole] = useState(() => selectedRole.name)
    let [description, setDescription] = useState(() => selectedRole.description)
    console.log(selectedRole.name)
    console.log(selectedRole.description)

    console.log(role)
    console.log(description)

    return (
        <Dialog onClose={onClose} aria-labelledby="simple-dialog-title" open={open} maxWidth="md">
            <DialogTitle id="simple-dialog-title">{title}</DialogTitle>
            <DialogContent>
                <FormInputTextField id="name"
                                    label="Role Name"
                                    type="text"
                                    value={role}
                                    handler={(value) => {
                                        setRole(value.target.value)
                                    }}/>

                <FormInputTextField id="description"
                                    label="Role Description" t
                                    ype="text"
                                    value={description}
                                    handler={(value) => {
                                        setDescription(value.target.value)
                                    }}/>

                <Separator/>

                <DialogActions>
                    <FormButton label="Save" onClickHandler={() => {
                        saveRoleFor({name: role, description: description})
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