import React from "react";
import {Grid, TextField} from "@mui/material";
import {DesktopDatePicker} from "@mui/x-date-pickers"
import {AdapterMoment} from '@mui/x-date-pickers/AdapterMoment';
import {LocalizationProvider} from '@mui/x-date-pickers/LocalizationProvider';

export default function FormDatePicker({label, value, onClickHandler}) {
    return <Grid container alignItems="flex-end">
        <Grid item md={true} sm={true} xs={true} justify="flex-end">
            <LocalizationProvider dateAdapter={AdapterMoment}>
                <DesktopDatePicker
                    label={label}
                    inputFormat={FormDateFormatPattern}
                    value={value}
                    onChange={onClickHandler || {}}
                    renderInput={(params) => <TextField {...params} fullWidth/>}
                />
            </LocalizationProvider>
        </Grid>
    </Grid>

}

export const FormDateFormatPattern = 'DD/MM/YYYY'
export const ApiDateFormatPattern = 'YYYY-MM-DD'