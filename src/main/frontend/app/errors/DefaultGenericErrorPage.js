var __assign = (this && this.__assign) || function () {
    __assign = Object.assign || function(t) {
        for (var s, i = 1, n = arguments.length; i < n; i++) {
            s = arguments[i];
            for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p))
                t[p] = s[p];
        }
        return t;
    };
    return __assign.apply(this, arguments);
};
import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { ThemeProvider, Typography } from "@mui/material";
import theme from "../component/styles";
import Template from "../component/Template";
import { SentimentVeryDissatisfied } from "@mui/icons-material";
import getDataFromDomUtils from "../utils/getDataFromDomUtils";
import ComponentInitializer from "../utils/ComponentInitializer";
var DefaultGenericErrorPage = function (_a) {
    var messages = _a.messages;
    var errors = JSON.parse(messages);
    return _jsx(ThemeProvider, __assign({ theme: theme }, { children: _jsx(Template, __assign({ maxWidth: "sm" }, { children: _jsxs(Typography, __assign({ variant: "h3", component: "h3" }, { children: [_jsx(SentimentVeryDissatisfied, { fontSize: "large" }), " ", errors.defaultMessage] })) })) }));
};
var errors = getDataFromDomUtils('errors');
var page = _jsx(DefaultGenericErrorPage, { messages: errors });
ComponentInitializer(page);
