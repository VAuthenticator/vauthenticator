import {Container, Paper} from "@material-ui/core";
import React from "react";
import Toolbar from "@material-ui/core/Toolbar";
import AppBar from "@material-ui/core/AppBar";
import IconButton from "@material-ui/core/IconButton";
import Typography from "@material-ui/core/Typography";
import MenuIcon from '@material-ui/icons/Menu';

export default (props) => {
    const {classes} = props;

    return (
        <div className={classes.root}>
            <AppBar position="static">
                <Toolbar variant="dense">
                    <IconButton edge="start" className={classes.menuButton} color="inherit" aria-label="menu">
                        <MenuIcon/>
                    </IconButton>
                    <Typography variant="h6" className={classes.title}>
                        VAuthenticator Administration
                    </Typography>
                </Toolbar>
            </AppBar>

            <Container maxWidth={props.maxWidth}>
                <Paper className={classes.padding} elevation={3}>
                    {props.children}
                </Paper>
            </Container>
        </div>
    )
}