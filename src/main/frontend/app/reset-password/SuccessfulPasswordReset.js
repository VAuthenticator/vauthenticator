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
import { Divider, Grid, ThemeProvider, Typography } from "@mui/material";
import theme from "../component/styles";
import Template from "../component/Template";
import { VpnKey } from "@mui/icons-material";
import ComponentInitializer from "../utils/ComponentInitializer";
var ResetPasswordMainPage = function () {
    return (_jsx(ThemeProvider, __assign({ theme: theme }, { children: _jsxs(Template, __assign({ maxWidth: "lg" }, { children: [_jsxs(Typography, __assign({ variant: "h3", component: "h3" }, { children: [_jsx(VpnKey, { fontSize: "large" }), " Reset Password"] })), _jsx(Grid, __assign({ style: { marginTop: '10px' } }, { children: _jsx(Divider, {}) })), _jsx(Typography, __assign({ variant: "h3", component: "h2" }, { children: "Password reset suceeded" }))] })) })));
};
var page = _jsx(ResetPasswordMainPage, {});
ComponentInitializer(page);
