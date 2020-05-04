import {Button, Grid} from "@material-ui/core";
import React from "react";

export default function FormButton({lable, type, onClickHandler}) {
    return <div dir="rtl">
        <Grid container alignItems="flex-end" style={{marginTop: '10px'}}>
            <Grid item md={true} sm={true} xs={true} justify="flex-end">
                <Button type={type || "button"}
                        variant="outlined"
                        color="primary"
                        onClick={onClickHandler || {}}
                        style={{textTransform: "none"}}>
                    {lable}
                </Button>
            </Grid>
        </Grid>
    </div>
}