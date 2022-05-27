import React from 'react';
import ReactDOM from 'react-dom';
import {Grid, withStyles} from "@material-ui/core";
import Typography from "@material-ui/core/Typography";
import {Fingerprint, Person, VpnKey} from "@material-ui/icons";
import Divider from "@material-ui/core/Divider";
import vauthenticatorStyles from "../component/styles";
import Template from "../component/Template";
import FormInputTextField from "../component/FormInputTextField";
import Separator from "../component/Separator";
import FormButton from "../component/FormButton";

const Login = withStyles(vauthenticatorStyles)((props) => {
    const {classes, rawFeatures} = props;

    let signUpLink = <div>
        <h3>are you not registered? if you want you can register  <a href="/sign-up">here</a></h3>
    </div>
    let features = JSON.parse(rawFeatures);

    return (
        <Template maxWidth="sm" classes={classes}>
            <Typography variant="h3" component="h3">
                <VpnKey fontSize="large"/> VAuthenticator
            </Typography>

            <Grid style={{marginTop: '10px'}}>
                <Divider/>
            </Grid>

            <form action="login" method="post">
                <div className={classes.margin}>
                    <FormInputTextField id="username"
                                        label="Username"
                                        type="email"
                                        suffix={<Person fontSize="large"/>}/>

                    <FormInputTextField id="password"
                                        label="password"
                                        type="password"
                                        suffix={<Fingerprint fontSize="large"/>}/>

                    <Separator/>

                    <FormButton type="submit" label="Login"/>
                </div>


                <Grid style={{marginTop: '10px'}}>
                    <Divider/>
                </Grid>


                <Grid style={{marginTop: '10px'}}>
                    {features.signup === true ? signUpLink : ""}
                </Grid>
            </form>
        </Template>
    )
})

if (document.getElementById('app')) {
    let features = document.getElementById('features').innerHTML
    ReactDOM.render(<Login rawFeatures={features}/>, document.getElementById('app'));
}