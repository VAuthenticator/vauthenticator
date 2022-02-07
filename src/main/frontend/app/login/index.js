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

const  featuresFrom = (rawFeatures) => {
    let features = {}
    rawFeatures.substring(1, rawFeatures.length - 1)
        .split(",")
        .forEach(feature => {
            let aux = feature.split("=")
            features[aux[0]] = aux[1]
        })
    return features;
}

const Login = withStyles(vauthenticatorStyles)((props) => {
    const {classes, rawFeatures} = props;

    let signUpLink = <a href="/vauthenticator/sign-up">Sign Up</a>
    let features = featuresFrom(rawFeatures);

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
                    {features.signUpLink === "true" ? signUpLink : ""}
                </Grid>
            </form>
        </Template>
    )
})

if (document.getElementById('app')) {
    let rawFeatures = document.getElementById('features').innerHTML
    let features = document.getElementById('features').innerHTML
    ReactDOM.render(<Login rawFeatures={features}/>, document.getElementById('app'));
}