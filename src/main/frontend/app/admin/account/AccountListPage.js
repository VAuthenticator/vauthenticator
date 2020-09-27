import React, {useEffect, useState} from 'react';
import {withStyles} from "@material-ui/core";
import vauthenticatorStyles from "../../component/styles";
import AdminTemplate from "../../component/AdminTemplate";
import StickyHeadTable from "../../component/StickyHeadTable";
import EditIcon from "@material-ui/icons/Edit";
import {findAllAccounts} from "./AccountRepository";
import Checkbox from "@material-ui/core/Checkbox";
import {Link} from "react-router-dom";

const columns = [
    {id: 'email', label: 'E-Mail', minWidth: 170},
    {id: 'enabled', label: 'Enabled', minWidth: 170},
    {id: 'accountLocked', label: 'Account Locked', minWidth: 170},
    {id: 'edit', label: 'Edit', minWidth: 170},
];

const AccountListPage = withStyles(vauthenticatorStyles)((props) => {
    const {classes} = props;
    const pageTitle = "Account Management"
    const [accounts, setAccounts] = useState([])

    const getEditLinkFor = (accountMail) => {
        return <Link to={`accounts/edit/${accountMail}`}
              style={{"textDecoration": "none"}}>
            <EditIcon/>
        </Link>;
    }

    const fetchAllAccounts = () => {
        findAllAccounts()
            .then(values => {
                let rows = values.map(value => {
                    console.log(value)
                    return {
                        email: value.email,
                        enabled: <Checkbox color="primary" checked={value.enabled}/>,
                        accountLocked: <Checkbox color="primary" checked={value.accountLocked}/>,
                        edit: getEditLinkFor(value["email"]),
                    }
                })

                setAccounts(rows)
            });
    }

    useEffect(() => {
        fetchAllAccounts()
    }, []);

    return (
        <AdminTemplate maxWidth="xl" classes={classes} page={pageTitle}>

            <StickyHeadTable columns={columns} rows={accounts}/>

        </AdminTemplate>
    )
})

export default AccountListPage