import {Button, Grid} from "@mui/material";
import React from "react";

export default function FormButton({labelPrefix, label, type, onClickHandler, direction}) {
    return <div dir={direction || ""}>
        <Grid container alignItems="flex-end" style={{marginTop: '10px'}}>
            <Grid item md={true} sm={true} xs={true} justify="flex-end">
                <Button type={type || "button"}
                        variant="outlined"
                        color="primary"
                        onClick={onClickHandler || {}}
                        style={{textTransform: "none"}}>
                    {labelPrefix} {label}
                </Button>
            </Grid>
        </Grid>
    </div>
}