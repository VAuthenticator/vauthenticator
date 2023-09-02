import {Button, Grid} from "@mui/material";
import React, {MouseEventHandler} from "react";

interface FormButtonProps {
    labelPrefix: any,
    label: string,
    type: any,
    onClickHandler: MouseEventHandler<HTMLButtonElement>,
    direction: string
}

const FormButton: React.FC<FormButtonProps> = ({labelPrefix, label, type, onClickHandler, direction}) => {
    return <div dir={direction || ""}>
        <Grid md={true} sm={true} xs={true}>
            <Grid container alignItems="flex-end" style={{marginTop: '10px'}}>
                <Button
                    type={type || "button"}
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

export default FormButton