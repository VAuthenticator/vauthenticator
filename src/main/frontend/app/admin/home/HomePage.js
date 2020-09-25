import React from 'react';
import {withStyles} from "@material-ui/core";
import vauthenticatorStyles from "../../component/styles";
import AdminTemplate from "../../component/AdminTemplate";
import MenuCard from "../../component/MenuCard";
import MenuCardContainer from "../../component/MenuCardContainer";
import {AssignmentInd, GroupAdd} from "@material-ui/icons";

const HomePage = withStyles(vauthenticatorStyles)((props) => {
    const {classes} = props;
    const pageTitle = "Home"
    return (
        <AdminTemplate maxWidth="xl" classes={classes} page={pageTitle}>
            <MenuCardContainer spacing={3}>
                <MenuCard linkTo="/client-applications/list"
                          content={
                              <p style={{textAlign: "center"}}>
                                  <GroupAdd style={{fontSize: 150}}/>
                                  <h1>Client Application Management Section</h1>
                              </p>
                          }/>
                <MenuCard linkTo="/roles"
                          content={
                              <p style={{textAlign: "center"}}>
                                  <AssignmentInd style={{fontSize: 150}}/>
                                  <h1>Role Management section</h1>
                              </p>}/>
                <MenuCard content={<h1>Account Management Section</h1>}/>
            </MenuCardContainer>

        </AdminTemplate>
    )
})

export default HomePage