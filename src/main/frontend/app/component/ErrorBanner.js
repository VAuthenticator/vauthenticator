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
import { jsx as _jsx } from "react/jsx-runtime";
import { Alert, Grid } from "@mui/material";
var ErrorBanner = function (_a) {
    var errorMessage = _a.errorMessage;
    return _jsx(Grid, __assign({ style: { marginTop: '10px' } }, { children: _jsx(Alert, __assign({ severity: "error" }, { children: errorMessage })) }));
};
export default ErrorBanner;
