import React, {useEffect, useState} from 'react';
import {withStyles} from "@material-ui/core";
import Typography from "@material-ui/core/Typography";
import vauthenticatorStyles from "../../component/styles";
import {useHistory, useParams} from "react-router";
import FormInputTextField from "../../component/FormInputTextField";
import AdminTemplate from "../../component/AdminTemplate";
import Separator from "../../component/Separator";
import {PeopleAlt} from "@material-ui/icons";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import Card from "@material-ui/core/Card";
import CheckboxesGroup from "../../component/CheckboxesGroup";
import {findAccountFor} from "./AccountRepository";

export default withStyles(vauthenticatorStyles)((props) => {
    const {classes} = props;
    let {accountMail} = useParams();
    const history = useHistory();
    const [email, setEmail] = useState(accountMail)
    const [enabled, setEnabled] = useState({enabled: false})
    const [accountLocked, setAccountLocked] = useState({accountLocked: false})

    let pageTitle = "Account Management";

    useEffect(() => {
        findAccountFor(email)
            .then(value => {
                setEnabled({enabled: value.enabled})
                setAccountLocked({accountLocked: value.accountLocked})
            })
    }, {})

    return (
        <AdminTemplate maxWidth="xl" classes={classes}
                       page={pageTitle}>

            <Typography variant="h3" component="h3">
                <PeopleAlt fontSize="large"/> Account mail: {accountMail}
            </Typography>

            <Card className={classes.card}>
                <CardHeader title="Account definition"
                            className={classes.title}
                            color="textSecondary"/>
                <CardContent>
                    <FormInputTextField id="email"
                                        label="Account Mail"
                                        required={true}
                                        handler={(value) => {
                                            setEmail(value.target.value)
                                        }}
                                        value={email}/>

                    <CheckboxesGroup id="enabled"
                                     handler={(value) => {
                                         setEnabled({enabled: value.target.checked})
                                     }}
                                     choicesRegistry={enabled}
                                     legend="Account Enabled"/>

                    <CheckboxesGroup id="accountLocked"
                                     handler={(value) => {
                                         setEnabled({accountLocked: value.target.checked})
                                     }}
                                     choicesRegistry={accountLocked}
                                     legend="Account Locked"/>
                    <Separator/>
                </CardContent>
            </Card>

        </AdminTemplate>
    );
})