import React, {useEffect} from 'react';
import {withStyles} from "@material-ui/core";
import Typography from "@material-ui/core/Typography";
import {GroupAdd} from "@material-ui/icons";
import vauthenticatorStyles from "../../component/styles";
import Template from "../../component/Template";
import StickyHeadTable from "../../component/StickyHeadTable";
import {findAllClientApplications} from "./ClientAppRepository";

const ClientAppManagementPage = withStyles(vauthenticatorStyles)((props) => {
    const {classes} = props;
    console.log("OK")
    const [applications, setApplications] = React.useState([])
    useEffect(() => {
        findAllClientApplications()
            .then(val => setApplications(val));
    },[]);


    return (
        <Template maxWidth="lg" classes={classes}>

            <Typography variant="h3" component="h3">
                <GroupAdd fontSize="large"/> VAuthenticator Client Application Admin
            </Typography>

            <StickyHeadTable rows={applications}/>

        </Template>
    );
})

export default ClientAppManagementPage