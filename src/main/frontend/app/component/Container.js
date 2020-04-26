import {Paper} from "@material-ui/core";
import Container from "@material-ui/core/Container";
import React from "react";

export default Container = (props) => {
    const {classes} = props;

    return (
        <Container maxWidth={props.maxWidth}>
            <Paper className={classes.padding} elevation={3}>
                {props.children}
            </Paper>
        </Container>
    )
}