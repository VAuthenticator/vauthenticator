import {Container, Link, Paper} from "@material-ui/core";
import React from "react";
import Toolbar from "@material-ui/core/Toolbar";
import AppBar from "@material-ui/core/AppBar";
import IconButton from "@material-ui/core/IconButton";
import Typography from "@material-ui/core/Typography";
import MenuIcon from '@material-ui/icons/Menu';
import {ExitToApp} from "@material-ui/icons";

export default (props) => {
    const {classes} = props;

    return (
        <div className={classes.root}>
            <AppBar position="static">
                <Toolbar variant="dense">
                    <Link href="#">
                        <IconButton edge="start"
                                    className={classes.menuButton}
                                    color="inherit"
                                    aria-label="menu">
                            <MenuIcon/>
                        </IconButton>
                    </Link>

                    <Typography variant="h6" className={classes.title}>
                        VAuthenticator Administration {props.page}
                    </Typography>

                    <Link href="/vauthenticator/logout">
                        <IconButton edge="start"
                                    className={classes.menuButton}
                                    color="inherit"
                                    aria-label="menu">
                            <ExitToApp/> Logout
                        </IconButton>
                    </Link>
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