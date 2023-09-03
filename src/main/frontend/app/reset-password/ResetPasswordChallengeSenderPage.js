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
import React from "react";
import { Box, Divider, Grid, ThemeProvider, Typography } from "@mui/material";
import theme from "../component/styles";
import Template from "../component/Template";
import { Person, VpnKey } from "@mui/icons-material";
import FormInputTextField from "../component/FormInputTextField";
import Separator from "../component/Separator";
import FormButton from "../component/FormButton";
import ComponentInitializer from "../utils/ComponentInitializer";
var ResetPasswordChallengeSender = function () {
    var _a = React.useState(""), email = _a[0], setEmail = _a[1];
    var sentResetPasswordChallenge = function (email) {
        return fetch("/api/reset-password-challenge", {
            method: "PUT",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ "mail": email }),
            credentials: 'same-origin'
        }).then(function (r) {
            if (r.status === 204) {
                window.location.href = "/reset-password/successful-reset-password-mail-challenge";
            }
        });
    };
    return (_jsx(ThemeProvider, __assign({ theme: theme }, { children: _jsxs(Template, __assign({ maxWidth: "sm" }, { children: [_jsxs(Typography, __assign({ variant: "h3", component: "h3" }, { children: [_jsx(VpnKey, { fontSize: "large" }), " Reset your password"] })), _jsx(Grid, __assign({ style: { marginTop: '10px' } }, { children: _jsx(Divider, {}) })), _jsxs(Box, { children: [_jsx(FormInputTextField, { id: "email", label: "Email", required: true, handler: function (value) {
                                setEmail(value.target.value);
                            }, value: email || "", suffix: _jsx(Person, { fontSize: "large" }) }), _jsx(Separator, {}), _jsx(FormButton, { type: "button", label: "Reset passwrd", onClickHandler: function () {
                                sentResetPasswordChallenge(email);
                            } })] })] })) })));
};
ComponentInitializer(_jsx(ResetPasswordChallengeSender, {}));
