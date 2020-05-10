import React, {useState} from "react";

import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContentText from "@material-ui/core/DialogContentText";
import FormInputTextField from "./FormInputTextField";
import FormButton from "./FormButton";
import Separator from "./Separator";
import DialogContent from "@material-ui/core/DialogContent";
import DialogActions from "@material-ui/core/DialogActions";
import {resetSecretKeyFor} from "../admin/clientapp/ClientAppRepository";

export default function ResetPasswordDialog({onClose, open, clientAppId}) {
    const [secretKey, setSecretKey] = useState("")
    return (
        <Dialog onClose={onClose} aria-labelledby="simple-dialog-title" open={open} maxWidth="lg">
            <DialogTitle id="simple-dialog-title">Reset Client App <b>{clientAppId}</b> Secret-Key</DialogTitle>
            <DialogContent>
                <DialogContentText id="alert-dialog-description">
                    Put hear the new client app secret-key for client app: <b>{clientAppId}</b>
                </DialogContentText>

                <FormInputTextField id="resetSecretKeyField"
                                    label="Client App Secret-Key"
                                    required={true}
                                    handler={(value) => {
                                        setSecretKey(value.target.value)
                                    }}/>

                <Separator/>

                <DialogActions>
                    <FormButton lable="Save" onClickHandler={() => {
                        resetSecretKeyFor(clientAppId, secretKey)
                            .then(value => onClose())
                    }}/>
                    <FormButton lable="Close" onClickHandler={onClose}/>
                </DialogActions>
            </DialogContent>
        </Dialog>
    );
}