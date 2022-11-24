import React from "react";
import Template from "../component/Template";
import FormInputTextField from "../component/FormInputTextField";
import FormButton from "../component/FormButton";

import {signUp} from "./SignUpRepository";
import {useNavigate} from "react-router";
import FormDatePicker, {ApiDateFormatPattern, FormDateFormatPattern} from "../component/FormDatePicker";
import {Divider, Grid, ThemeProvider, Typography} from "@mui/material";
import {GroupAdd, VpnKey} from "@mui/icons-material";
import moment from "moment/moment";
import theme from "../component/styles";
import FormInputMask from "../component/FormInputMask";

const SignUpPage = () => {
    const [email, setEmail] = React.useState("")
    const [password, setPassword] = React.useState("")
    const [firstName, setFirstName] = React.useState("")
    const [lastName, setLastName] = React.useState("")
    const [birthDate, setBirthDate] = React.useState("")
    const [phone, setPhone] = React.useState("")

    let navigate = useNavigate();

    return (
        <ThemeProvider theme={theme}>
            <Template maxWidth="sm">
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
                    value={birthDate}
                    pattern={ApiDateFormatPattern}
                    onClickHandler={(value) => {
                        console.log(value)
                        let date = "";
                        try {
                            date = value.format(ApiDateFormatPattern);
                            console.log("date: " + date)

                            setBirthDate(date)
                        } catch (e) {
                            console.error(e)
                        }
                    }}
                    label="Birth Date"/>


                <FormInputMask id="phone"
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
        </ThemeProvider>
    )
}

export default SignUpPage