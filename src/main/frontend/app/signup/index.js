import React from 'react';
import ReactDOM from 'react-dom';
import {Grid, withStyles} from "@material-ui/core";
import Typography from "@material-ui/core/Typography";
import {AssignmentInd, VpnKey} from "@material-ui/icons";
import Divider from "@material-ui/core/Divider";
import vauthenticatorStyles from "../component/styles";
import Template from "../component/Template";
import FormInputTextField from "../component/FormInputTextField";
import FormButton from "../component/FormButton";

const Login = withStyles(vauthenticatorStyles)((props) => {
    const {classes} = props;

    const [email, setEmail] = React.useState("")
    const [password, setPassword] = React.useState("")
    const [firstName, setFirstName] = React.useState("")
    const [lastName, setLastName] = React.useState("")

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

                        }}
                        labelPrefix={<AssignmentInd fontSize="large"/>}
                        label={"Sign Up"}/>
        </Template>
    )
})

if (document.getElementById('app')) {
    let features = document.getElementById('features').innerHTML
    ReactDOM.render(<Login features={features}/>, document.getElementById('app'));
}