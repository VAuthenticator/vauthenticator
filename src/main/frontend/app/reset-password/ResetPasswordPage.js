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
import React from 'react';
import Template from "../component/Template";
import FormInputTextField from "../component/FormInputTextField";
import Separator from "../component/Separator";
import FormButton from "../component/FormButton";
import { Divider, Grid, Paper, ThemeProvider, Typography } from "@mui/material";
import { Person, VpnKey } from "@mui/icons-material";
import theme from "../component/styles";
import getDataFromDomUtils from "../utils/getDataFromDomUtils";
import ComponentInitializer from "../utils/ComponentInitializer";
var ResetPasswordPage = function (_a) {
    var metadata = _a.metadata;
    var _b = React.useState(""), password = _b[0], setPassword = _b[1];
    var resetPassword = function (ticket, password) {
        return fetch("/api/reset-password/".concat(ticket), {
            method: "PUT",
            credentials: 'same-origin',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                newPassword: password
            })
        }).then(function (r) {
            console.log("send reset password");
            if (r.status === 204) {
                window.document.location.href = "/reset-password/successful-password-reset";
            }
        });
    };
    return (_jsx(ThemeProvider, __assign({ theme: theme }, { children: _jsxs(Template, __assign({ maxWidth: "sm" }, { children: [_jsxs(Typography, __assign({ variant: "h3", component: "h3" }, { children: [_jsx(VpnKey, { fontSize: "large" }), " Reset your password"] })), _jsx(Grid, __assign({ style: { marginTop: '10px' } }, { children: _jsx(Divider, {}) })), _jsxs(Paper, { children: [_jsx(FormInputTextField, { id: "newPassword", label: "New Password", type: "Password", required: true, handler: function (value) {
                                setPassword(value.target.value);
                            }, value: password || "", suffix: _jsx(Person, { fontSize: "large" }) }), _jsx(Separator, {}), _jsx(FormButton, { type: "button", label: "Reset passwrd", onClickHandler: function () { return resetPassword(JSON.parse(metadata)["ticket"], password); } })] })] })) })));
};
var metadata = getDataFromDomUtils('metadata');
var page = _jsx(ResetPasswordPage, { metadata: metadata });
ComponentInitializer(page);
