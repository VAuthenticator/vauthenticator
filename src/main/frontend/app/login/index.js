import React from 'react';
import ReactDOM from 'react-dom';
import theme from "../component/styles";
import Template from "../component/Template";
import FormInputTextField from "../component/FormInputTextField";
import Separator from "../component/Separator";
import FormButton from "../component/FormButton";
import {HashRouter, Link} from "react-router-dom";
import {Route, Routes, useNavigate} from "react-router";
import {Box, Divider, Grid, ThemeProvider, Typography} from "@mui/material";
import {Fingerprint, Person, VpnKey} from "@mui/icons-material";

const LoginMainPage = (props) => {
    return (
        <HashRouter>
            <Routes>
                <Route index path="/"
                       element={<Login {...props} />}/>
                <Route exact={true} path="/reset-password-challenge"
                       element={<ResetPasswordChallengeSender {...props} />}/>
                <Route exact={true} path="/reset-password-challenge-sent"
                       element={<SuccessfulResetPasswordMailChallenge {...props} />}/>
            </Routes>
        </HashRouter>)
}


const Login = (props) => {
    const {rawFeatures} = props;
    let signUpLink = <div>
        <h3>are you not registered? if you want you can register <a href="/sign-up">here</a></h3>
    </div>
    let resetPasswordLink = <div>
        <h3>do you have forgot your password? please click <Link to={'/reset-password-challenge'}>here</Link> to recover
            your
            password</h3>
    </div>
    let features = JSON.parse(rawFeatures);

    return (
        <ThemeProvider theme={theme}>

            <Template maxWidth="sm">
                <Typography variant="h3" component="h3">
                    <VpnKey fontSize="large"/> VAuthenticator
                </Typography>

                <Grid style={{marginTop: '10px'}}>
                    <Divider/>
                </Grid>

                {<form action="login" method="post">
                    <Box>
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
                    </Box>


                    <Grid style={{marginTop: '10px'}}>
                        <Divider/>
                    </Grid>


                    <Grid style={{marginTop: '10px'}}>
                        {features.signup === true ? signUpLink : ""}
                        {features["reset-password"] === true ? resetPasswordLink : ""}
                    </Grid>
                </form>}
            </Template>
        </ThemeProvider>

    )
}


const ResetPasswordChallengeSender = (props) => {
    const [email, setEmail] = React.useState("")
    let navigate = useNavigate();

    const sentResetPasswordChallenge = (email) => {
        return fetch(`/api/mail/${email}/rest-password-challenge`, {
            method: "PUT",
            credentials: 'same-origin'
        }).then(r => {
            if (r.status === 204) {
                navigate("/reset-password-challenge-sent", {replace: true});
            }
        })
    }

    return (
        <ThemeProvider theme={theme}>
            <Template maxWidth="sm">
                <Typography variant="h3" component="h3">
                    <VpnKey fontSize="large"/> Reset your password
                </Typography>

                <Grid style={{marginTop: '10px'}}>
                    <Divider/>
                </Grid>

                <Box>
                    <FormInputTextField id="email"
                                        label="Email"

                                        required={true}
                                        handler={(value) => {
                                            setEmail(value.target.value)
                                        }}
                                        value={email || ""}
                                        suffix={<Person fontSize="large"/>}/>

                    <Separator/>

                    <FormButton type="button" label="Reset passwrd" onClickHandler={sentResetPasswordChallenge(email)}/>

                </Box>

            </Template>
        </ThemeProvider>
    )
}

const SuccessfulResetPasswordMailChallenge = () => {
    return (
        <ThemeProvider theme={theme}>
            <Template maxWidth="lg">
                <Typography variant="h3" component="h3">
                    <VpnKey fontSize="large"/> Reset Password
                </Typography>

                <Grid style={{marginTop: '10px'}}>
                    <Divider/>
                </Grid>

                <Typography variant="h3" component="h3">
                    We are sent an email on your account inbox please follow the instruction on the mail to reset yout
                    password
                </Typography>
            </Template>
        </ThemeProvider>
    )
}


if (document.getElementById('app')) {
    let features = document.getElementById('features').innerHTML
    ReactDOM.render(<LoginMainPage rawFeatures={features}/>, document.getElementById('app'));
}