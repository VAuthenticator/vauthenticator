import * as React from 'react';
import PropTypes from 'prop-types';
import {IMaskInput} from 'react-imask';
import {Grid, TextField} from "@mui/material";

const InputMask = React.forwardRef(function TextMaskCustom(props, ref) {
    const {onChange, ...other} = props;
    return (
        <IMaskInput
            {...other}
            variant="outlined"

            mask="+00 000 0000000"
            definitions={{
                '#': /[1-9]/,
            }}
            inputRef={ref}
            onAccept={(value) => onChange({target: {name: props.name, value}})}
            overwrite
        />
    );
});

InputMask.propTypes = {
    name: PropTypes.string.isRequired,
    onChange: PropTypes.func.isRequired,
};

export default function FormInputMask({id, label, required, value, handler}) {
    return <Grid container spacing={8} alignItems="flex-end">
        <Grid item md={true} sm={true} xs={true}>
            <TextField
                fullWidth
                label={label}
                variant="outlined"
                required={required}
                value={value}
                onChange={handler}
                name={id}
                id={id}
                InputProps={{
                    inputComponent: InputMask,
                }}
            />
        </Grid>
    </Grid>
}