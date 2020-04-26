import React from 'react';
import ReactDOM from 'react-dom';
import {Button, Grid, TextField, withStyles} from "@material-ui/core";
import Typography from "@material-ui/core/Typography";
import {Fingerprint, Person, VpnKey} from "@material-ui/icons";
import Divider from "@material-ui/core/Divider";
import vauthenticatorStyles from "../component/styles";
import Container from "../component/Container";

const Login = withStyles(vauthenticatorStyles)((props) => {
    const {classes} = props;

    return (
        <Container maxWidth="sm">
            <Typography variant="h3" component="h3">
                <VpnKey fontSize="large"/> VAuthenticator
            </Typography>

            <Grid style={{marginTop: '10px'}}>
                <Divider/>
            </Grid>

            <form action="login" method="post">
                <div className={classes.margin}>
                    <Grid container spacing={8} alignItems="flex-end">
                        <Grid item>
                            <Person fontSize="large"/>
                        </Grid>
                        <Grid item md={true} sm={true} xs={true}>
                            <TextField name="username" id="username" label="Username" type="email"
                                       variant="outlined" fullWidth autoFocus required/>
                        </Grid>
                    </Grid>
                    <Grid container spacing={8} alignItems="flex-end">
                        <Grid item>
                            <Fingerprint fontSize="large"/>
                        </Grid>
                        <Grid item md={true} sm={true} xs={true}>
                            <TextField name="password" id="password" label="Password" type="password"
                                       variant="outlined" fullWidth required/>
                        </Grid>
                    </Grid>

                    <Grid style={{marginTop: '10px'}}>
                        <Divider/>
                    </Grid>

                    <div dir="rtl">
                        <Grid container alignItems="flex-end" style={{marginTop: '10px'}}>
                            <Grid item md={true} sm={true} xs={true} justify="flex-end">
                                <Button type={"submit"} variant="outlined" color="primary"
                                        style={{textTransform: "none"}}>Login</Button>
                            </Grid>
                        </Grid>
                    </div>

                </div>
            </form>
        </Container>
    )
})

if (document.getElementById('app')) {
    ReactDOM.render(<Login/>, document.getElementById('app'));
}