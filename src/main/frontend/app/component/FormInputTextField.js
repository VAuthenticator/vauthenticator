import {Grid, TextField} from "@mui/material";
import React from "react";

export default function FormInputTextField({id, label, type, required, autoFocus, disabled, suffix, value, handler}) {
    return <Grid container spacing={8} alignItems="flex-end">
        {suffix && <Grid item>
            {suffix}
        </Grid>}
        <Grid item md={true} sm={true} xs={true}>
            <TextField name={id} id={id} label={label} type={type || "text"} disabled={disabled}
                       variant="outlined" fullWidth autoFocus={autoFocus} required={required || false}
                       value={value}
                       onChange={handler}/>
        </Grid>
    </Grid>
}