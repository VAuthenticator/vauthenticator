import {Alert, Grid} from "@mui/material";
import React from "react";

const ErrorBanner = ({errorMessage}) => {
    return <Grid style={{marginTop: '10px'}}>
        <Alert severity="error">{errorMessage}</Alert>
    </Grid>

}

export default ErrorBanner