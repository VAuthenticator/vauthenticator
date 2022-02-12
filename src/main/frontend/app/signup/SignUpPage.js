import {Grid, withStyles} from "@material-ui/core";
import React from "react";
import Template from "../component/Template";
import Typography from "@material-ui/core/Typography";
import {GroupAdd, VpnKey} from "@material-ui/icons";
import Divider from "@material-ui/core/Divider";
import vauthenticatorStyles from "../component/styles";
import FormInputTextField from "../component/FormInputTextField";
import FormButton from "../component/FormButton";
import {useHistory} from 'react-router-dom';
import {signUp} from "./SignUpRepository";

const SignUpPage = withStyles(vauthenticatorStyles)((props) => {
    const {classes} = props;

    const [email, setEmail] = React.useState("")
    const [password, setPassword] = React.useState("")
    const [firstName, setFirstName] = React.useState("")
    const [lastName, setLastName] = React.useState("")
    const history = useHistory();

    return (
        <Template maxWidth="sm" classes={classes}>
            <Typography variant="h3" component="h3">
                <VpnKey fontSize="large"/> Sign Up on VAuthenticator
            </Typography>

            <Grid style={{marginTop: '10px'}}>
                <Divider/>
            </Grid>

            <FormInputTextField id="email"
                                label="Email"
                                required={true}
                                handler={(value) => {
                                    setEmail(value.target.value)
                                }}
                                value={email || ""}/>

            <FormInputTextField id="password"
                                label="Password"
                                type="Password"
                                required={true}
                                handler={(value) => {
                                    setPassword(value.target.value)
                                }}
                                value={password || ""}/>

            <FormInputTextField id="firstName"
                                label="First Name"
                                required={true}
                                handler={(value) => {
                                    setFirstName(value.target.value)
                                }}
                                value={firstName || ""}/>

            <FormInputTextField id="lastName"
                                label="Last Name"
                                required={true}
                                handler={(value) => {
                                    setLastName(value.target.value)
                                }}
                                value={lastName || ""}/>


            <Grid style={{marginTop: '10px'}}>
                <Divider/>
            </Grid>
            <FormButton type="button"
                        onClickHandler={() => {
                            signUp().then(r => history.push("/succeeded"))
                        }}
                        labelPrefix={<GroupAdd fontSize="large"/>}
                        label={"Sign Up"}/>
        </Template>
    )
})


export default SignUpPage