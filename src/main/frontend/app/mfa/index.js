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
import { Box, Divider, Grid, ThemeProvider, Typography } from "@mui/material";
import theme from "../component/styles";
import Template from "../component/Template";
import { Person, VpnKey } from "@mui/icons-material";
import FormInputTextField from "../component/FormInputTextField";
import Separator from "../component/Separator";
import FormButton from "../component/FormButton";
import ErrorBanner from "../component/ErrorBanner";
import getDataFromDomUtils from "../utils/getDataFromDomUtils";
import ComponentInitializer from "../utils/ComponentInitializer";
var MfaChallengePage = function (_a) {
    var rawErrors = _a.rawErrors;
    var sendAgainMfaCode = function () {
        fetch("/mfa-challenge/send", {
            method: 'PUT',
            credentials: 'same-origin', // include, *same-origin, omit
        });
    };
    var errorMessage = JSON.parse(rawErrors)["mfa-challenge"];
    var errorsBanner = _jsx(ErrorBanner, { errorMessage: errorMessage });
    return (_jsx(ThemeProvider, __assign({ theme: theme }, { children: _jsxs(Template, __assign({ maxWidth: "sm" }, { children: [_jsxs(Typography, __assign({ variant: "h3", component: "h3" }, { children: [_jsx(VpnKey, { fontSize: "large" }), " VAuthenticator MFA module"] })), _jsx(Grid, __assign({ style: { marginTop: '10px' } }, { children: _jsx(Divider, {}) })), errorMessage ? errorsBanner : "", _jsx("form", __assign({ action: "mfa-challenge", method: "post" }, { children: _jsxs(Box, { children: [_jsx(FormInputTextField, { id: "mfa-code", label: "mfa-code", type: "text", suffix: _jsx(Person, { fontSize: "large" }) }), _jsx(Separator, {}), _jsx(FormButton, { type: "submit", label: "Login" }), _jsx(FormButton, { type: "button", label: "Send again code", onClickHandler: function () { return sendAgainMfaCode(); } })] }) }))] })) })));
};
var errors = getDataFromDomUtils('errors');
var page = _jsx(MfaChallengePage, { rawErrors: errors });
ComponentInitializer(page);
