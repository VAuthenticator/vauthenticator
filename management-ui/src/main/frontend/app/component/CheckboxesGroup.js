import React from 'react';
import {makeStyles} from '@material-ui/core/styles';
import FormLabel from '@material-ui/core/FormLabel';
import FormControl from '@material-ui/core/FormControl';
import FormGroup from '@material-ui/core/FormGroup';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Checkbox from '@material-ui/core/Checkbox';

const useStyles = makeStyles((theme) => ({
    root: {
        display: 'flex',
    },
    formControl: {
        margin: theme.spacing(3),
    },
}));
// choicesRegistry = [{name, checked, label}, ...]
export default function CheckboxesGroup({id, legend, choicesRegistry, handler}) {
    const classes = useStyles()
    const choicesRegistryKeys = Object.keys(choicesRegistry)
    return (
        <div className={classes.root}>
            <FormControl id={id} name={id} component="fieldset" className={classes.formControl}>
                <FormLabel component="legend">{legend}</FormLabel>
                <FormGroup>
                    {choicesRegistryKeys.map(choiceKey => {
                        return <FormControlLabel
                                control={<Checkbox checked={choicesRegistry[choiceKey]} onChange={handler} name={choiceKey}/>}
                                label={choiceKey}
                            />
                        }
                    )}
                </FormGroup>
            </FormControl>
        </div>
    );
}