import React from "react";
import Template from "../component/Template";
import vauthenticatorStyles from "../component/styles";
import FormInputTextField from "../component/FormInputTextField";
import FormButton from "../component/FormButton";

import {signUp} from "./SignUpRepository";
import {useNavigate} from "react-router";
import FormDatePicker, {DateFormatPattern} from "../component/FormDatePicker";
import {Divider, Grid, Typography, withStyles} from "@mui/material";
import {VpnKey} from "@mui/icons-material";
import moment from "moment/moment";

const SignUpPage = withStyles(vauthenticatorStyles)((props) => {
    const {classes} = props;

    const [email, setEmail] = React.useState("")
    const [password, setPassword] = React.useState("")
    const [firstName, setFirstName] = React.useState("")
    const [lastName, setLastName] = React.useState("")
    const [birthDate, setBirthDate] = React.useState("")
    const [phone, setPhone] = React.useState("")

    let navigate = useNavigate();

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

            <FormDatePicker
                value={moment(birthDate, DateFormatPattern)}
                onClickHandler={(value) => {
                    let date = "";
                    try {
                        date = value.format(DateFormatPattern);
                    } catch (e) {
                    }
                    setBirthDate(date)
                }}
                label="Birth Date"/>


            <FormInputTextField id="phone"
                                label="Phone"
                                required={true}
                                handler={(value) => {
                                    setPhone(value.target.value)
                                }}
                                value={phone || ""}/>

            <Grid style={{marginTop: '10px'}}>
                <Divider/>
            </Grid>
            <FormButton type="button"
                        onClickHandler={() => {
                            signUp({
                                "email": email,
                                "password": password,
                                "firstName": firstName,
                                "lastName": lastName,
                                "phone": phone,
                                "birthDate": birthDate
                            })
                                .then(r => {
                                    if (r.status === 201) {
                                        navigate("/succeeded", {replace: true});
                                    }
                                })
                        }}
                        labelPrefix={<GroupAdd fontSize="large"/>}
                        label={"Sign Up"}/>
        </Template>
    )
})


export default SignUpPage