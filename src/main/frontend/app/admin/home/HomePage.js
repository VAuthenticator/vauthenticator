import React from 'react';
import {withStyles} from "@material-ui/core";
import vauthenticatorStyles from "../../component/styles";
import AdminTemplate from "../../component/AdminTemplate";
import MenuCardContainer from "../../component/MenuCardContainer";
import HomePageMenuItem, {homeMenuContent} from "./HomePageMenuItem";

const HomePage = withStyles(vauthenticatorStyles)((props) => {
    const {classes} = props;
    const pageTitle = "Home"
    return (
        <AdminTemplate maxWidth="xl" classes={classes} page={pageTitle}>
            <MenuCardContainer spacing={3}>
                <HomePageMenuItem content={homeMenuContent.clientApplications} />
                <HomePageMenuItem content={homeMenuContent.roles} />
                <HomePageMenuItem content={homeMenuContent.accounts} />
            </MenuCardContainer>

        </AdminTemplate>
    )
})

export default HomePage