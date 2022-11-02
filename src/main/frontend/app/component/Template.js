import React from "react";
import {Container, Paper} from "@mui/material";

export default (props) => {

    return (
        <Container maxWidth={props.maxWidth}>
            <Paper elevation={3}>
                {props.children}
            </Paper>
        </Container>
    )
}