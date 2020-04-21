import React from 'react';
import {Button, Grid, Paper, TextField, withStyles} from '@material-ui/core';
import {Fingerprint, Person, VpnKey} from '@material-ui/icons'
import Container from "@material-ui/core/Container";
import Divider from "@material-ui/core/Divider";
import Typography from "@material-ui/core/Typography";

const styles = theme => ({
    margin: {
        margin: theme.spacing.unit * 2,
    },
    padding: {
        padding: theme.spacing.unit
    }
});

class Login extends React.Component {
    render() {
        const {classes} = this.props;
        return (
            <Container maxWidth="sm">
                <Paper className={classes.padding}>
                    <Typography variant="h3" component="h3">
                        <VpnKey  fontSize="large"/> VAuthenticator
                    </Typography>

                    <Divider />

                    <form action="login" method="POST">
                        <div className={classes.margin}>
                            <Grid container spacing={8} alignItems="flex-end">
                                <Grid item>
                                    <Person fontSize="large"/>
                                </Grid>
                                <Grid item md={true} sm={true} xs={true}>
                                    <TextField id="username" label="Username" type="email" variant="outlined"  fullWidth autoFocus required/>
                                </Grid>
                            </Grid>
                            <Grid container spacing={8} alignItems="flex-end">
                                <Grid item>
                                    <Fingerprint fontSize="large"/>
                                </Grid>
                                <Grid item md={true} sm={true} xs={true}>
                                    <TextField id="username" label="Password" type="password" variant="outlined" fullWidth required/>
                                </Grid>
                            </Grid>

                            <Grid style={{marginTop: '10px'}}>
                                <Divider />
                            </Grid>

                            <div dir="rtl">
                                <Grid container alignItems="flex-end" style={{marginTop: '10px'}}>
                                    <Grid item md={true} sm={true} xs={true} justify="flex-end">
                                        <Button type={"submit"} variant="outlined" color="primary" style={{textTransform: "none"}}>Login</Button>
                                    </Grid>
                                </Grid>
                            </div>

                        </div>
                    </form>
                </Paper>
            </Container>
        );
    }
}

export default withStyles(styles)(Login);