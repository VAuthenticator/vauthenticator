import {ThemeProvider, Typography} from "@mui/material";
import theme from "../component/styles";
import Template from "../component/Template";
import React from "react";
import {SentimentVeryDissatisfied} from "@mui/icons-material";

const DefaultGenericErrorPage = ({messages}) => {
    const errors = JSON.parse(messages)
    return <ThemeProvider theme={theme}>
        <Template maxWidth="sm">
            <Typography variant="h3" component="h3">
                <SentimentVeryDissatisfied fontSize="large"/> {errors.defaultMessage}
            </Typography>

        </Template>
    </ThemeProvider>

}

export default DefaultGenericErrorPage