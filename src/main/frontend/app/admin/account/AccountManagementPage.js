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
import {findAccountFor, saveAccountFor} from "./AccountRepository";
import FormButton from "../../component/FormButton";
import StickyHeadTable from "../../component/StickyHeadTable";
import {findAllRoles} from "../roles/RoleRepository";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Checkbox from "@material-ui/core/Checkbox";

const columns = [
    {id: 'name', label: 'Role', minWidth: 170},
    {id: 'description', label: 'Description Role', minWidth: 170},
    {id: 'delete', label: 'Delete Role', minWidth: 170}
];

export default withStyles(vauthenticatorStyles)((props) => {
    const {classes} = props;
    let {accountMail} = useParams();
    const history = useHistory();
    const [email, setEmail] = useState(accountMail)
    const [enabled, setEnabled] = useState({enabled: false})
    const [accountLocked, setAccountLocked] = useState({accountLocked: false})
    const [authorities, setAuthorities] = useState([])
    let [roles, setRoles] = useState([])

    let pageTitle = "Account Management";

    const rolesRow = (authoritiesValues, roleValues) => roleValues.map(role => {
        return {
            name: role.name,
            description: role.description,
            delete: <FormControlLabel control={
                <Checkbox onChange={() => {
                    const roleIndex = authoritiesValues.indexOf(role.name)

                    if (roleIndex !== -1) {
                        authoritiesValues.splice(roleIndex, 1)
                    } else {
                        authoritiesValues.push(role.name)
                    }

                    setAuthorities(rolesRow(authoritiesValues, roleValues))
                }}
                          checked={authoritiesValues.indexOf(role.name) !== -1}/>
            }/>
        }
    })

    useEffect(() => {
            findAllRoles()
                .then(roleValues => {
                    console.log(roles)
                    findAccountFor(email)
                        .then(value => {
                            setEnabled({enabled: value.enabled})
                            setAccountLocked({accountLocked: value.accountLocked})
                            setRoles(roles)
                            setAuthorities(
                                rolesRow(value.authorities, roleValues)
                            )
                        })
                })
        },
        {}
    )

    const save = () => {
        const account = {
            email: email,
            enabled: enabled.enabled,
            accountLocked: accountLocked.accountLocked,
            authorities: authorities.map(auth => auth.name)
        }

        saveAccountFor(account)
            .then(response => {
                if (response.status === 204) {
                    history.goBack();
                }
            })
    }

    return (
        <AdminTemplate maxWidth="xl" classes={classes}
                       page={pageTitle}>

            <p>{roles.length}</p>
            <p>{authorities.length}</p>

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
                                        disabled={true}
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
                                         setAccountLocked({accountLocked: value.target.checked})
                                     }}
                                     choicesRegistry={accountLocked}
                                     legend="Account Locked"/>

                    <StickyHeadTable columns={columns} rows={authorities}/>

                    <Separator/>
                    <FormButton label="Save" onClickHandler={save}/>
                </CardContent>
            </Card>

        </AdminTemplate>
    );
})