import React, {useEffect} from 'react';
import {withStyles} from "@material-ui/core";
import Typography from "@material-ui/core/Typography";
import {GroupAdd} from "@material-ui/icons";
import vauthenticatorStyles from "../../component/styles";
import Template from "../../component/Template";
import StickyHeadTable from "../../component/StickyHeadTable";
import {findAllClientApplications} from "./ClientAppRepository";
import Link from "react-router-dom/Link";
import EditIcon from "@material-ui/icons/Edit";

const columns = [
    {id: 'clientAppName', label: 'Client Application Name', minWidth: 170},
    {id: 'clientAppId', label: 'Client Application Id', minWidth: 170},
    {id: 'scopes', label: 'Client Scopes', minWidth: 170},
    {id: 'authorizedGrantTypes', label: 'Client Application Autorized Grant Type', minWidth: 170},
    {id: 'federation', label: 'Client Application Federation', minWidth: 170},
    {id: 'edit', label: 'Edit Application', minWidth: 170}
];
const getEditLinkFor = (clientAppId) => {
    return <Link to={`client-application/${clientAppId}`}
                 style={{"text-decoration": "none"}}>
        <EditIcon/>
    </Link>;
}
const ClientAppManagementPage = withStyles(vauthenticatorStyles)((props) => {
    const {classes} = props;
    const [applications, setApplications] = React.useState([])

    useEffect(() => {
        findAllClientApplications()
            .then(val => {
                let rows = val.map(value => {


                    value.edit = getEditLinkFor(value["clientAppId"])
                    return value
                })
                setApplications(rows)
            });
    }, []);


    return (
        <Template maxWidth="lg" classes={classes}>

            <Typography variant="h3" component="h3">
                <GroupAdd fontSize="large"/> VAuthenticator Client Application Admin
            </Typography>

            <StickyHeadTable columns={columns} rows={applications}/>

        </Template>
    );
})

export default ClientAppManagementPage