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
import { Mail } from "@mui/icons-material";
import getDataFromDomUtils from "../utils/getDataFromDomUtils";
import ComponentInitializer from "../utils/ComponentInitializer";
var SuccessfulMailVerifyPage = function (_a) {
    var rawFeatures = _a.rawFeatures;
    return (_jsx(ThemeProvider, __assign({ theme: theme }, { children: _jsxs(Template, __assign({ maxWidth: "lg" }, { children: [_jsxs(Typography, __assign({ variant: "h3", component: "h3" }, { children: [_jsx(Mail, { fontSize: "large" }), " Confirmation of your email verification"] })), _jsx(Grid, __assign({ style: { marginTop: '10px' } }, { children: _jsx(Divider, {}) })), _jsx(Typography, __assign({ variant: "h3", component: "h2" }, { children: "Your email has been successful verified." }))] })) })));
};
var features = getDataFromDomUtils('features');
var page = _jsx(SuccessfulMailVerifyPage, { rawFeatures: features });
ComponentInitializer(page);
