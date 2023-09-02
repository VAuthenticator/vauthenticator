import React from "react";
import ReactDOM from "react-dom";


import {ThemeProvider, Typography} from "@mui/material";
import theme from "../component/styles";
import Template from "../component/Template";
import {SentimentVeryDissatisfied} from "@mui/icons-material";
import getDataFromDomUtils from "../utils/getDataFromDomUtils";

interface DefaultGenericErrorPageProps {
    messages: string
}

interface ErrorMessage {
    defaultMessage: string
}

const DefaultGenericErrorPage: React.FC<DefaultGenericErrorPageProps> = ({messages}) => {
    let errors: ErrorMessage = JSON.parse(messages)
    return <ThemeProvider theme={theme}>
        <Template maxWidth="sm">
            <Typography variant="h3" component="h3">
                <SentimentVeryDissatisfied fontSize="large"/> {errors.defaultMessage}
            </Typography>

        </Template>
    </ThemeProvider>

}

if (document.getElementById('app')) {
    let errors: string = getDataFromDomUtils('errors')
    ReactDOM.render(<DefaultGenericErrorPage messages={errors}/>, document.getElementById('app'));
}