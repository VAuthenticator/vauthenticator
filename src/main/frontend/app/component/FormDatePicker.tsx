import React from "react";
import {Grid} from "@mui/material";
import {DesktopDatePicker} from "@mui/x-date-pickers"
import {AdapterMoment} from '@mui/x-date-pickers/AdapterMoment';
import {LocalizationProvider} from '@mui/x-date-pickers/LocalizationProvider';
import moment from 'moment';

interface FormDatePickerProps {
    label: string
    value: string
    onClickHandler: () => void
    pattern: string
}

const FormDatePicker: React.FC<FormDatePickerProps> = ({label, value, onClickHandler, pattern}) => {
    let val = value && moment(value, pattern)
    console.log("val: " + val)
    return <Grid container alignItems="flex-end">
        <Grid item md={true} sm={true} xs={true}>
            <LocalizationProvider dateAdapter={AdapterMoment}>
                <DesktopDatePicker
                    label={label}
                    format={FormDateFormatPattern}
                    onChange={onClickHandler || {}}
                    value={val}
                />
            </LocalizationProvider>
        </Grid>
    </Grid>

}

export const FormDateFormatPattern: string = 'DD/MM/YYYY'
export const ApiDateFormatPattern: string = 'YYYY-MM-DD'

export default FormDatePicker