import React from 'react';
import Grid from "@material-ui/core/Grid";

export default function MenuCardContainer({children, space}) {
    return (
        <Grid container spacing={space}>
            {children}
        </Grid>
        );
}