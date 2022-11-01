import React from "react";
import {Grid, TextField, useTheme} from "@mui/material";
import {DesktopDatePicker} from "@mui/x-date-pickers"
import {AdapterMoment} from '@mui/x-date-pickers/AdapterMoment';
import {LocalizationProvider} from '@mui/x-date-pickers/LocalizationProvider';

export default function FormDatePicker({label, value, onClickHandler}) {
    const theme = useTheme()
    return <Grid container alignItems="flex-end" style={theme.formDatePicker}>
        <Grid item md={true} sm={true} xs={true} justify="flex-end">
            <LocalizationProvider dateAdapter={AdapterMoment}>
                <DesktopDatePicker
                    label={label}
                    inputFormat={DateFormatPattern}
                    value={value}
                    onChange={onClickHandler || {}}
                    renderInput={(params) => <TextField {...params} fullWidth />}
                />
            </LocalizationProvider>
        </Grid>
    </Grid>

}

export const DateFormatPattern = 'DD/MM/YYYY'