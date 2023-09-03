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
import { jsxs as _jsxs, jsx as _jsx } from "react/jsx-runtime";
import { Button, Grid } from "@mui/material";
var FormButton = function (_a) {
    var labelPrefix = _a.labelPrefix, label = _a.label, type = _a.type, onClickHandler = _a.onClickHandler, direction = _a.direction;
    return _jsx("div", __assign({ dir: direction || "" }, { children: _jsx(Grid, __assign({ md: true, sm: true, xs: true }, { children: _jsx(Grid, __assign({ container: true, alignItems: "flex-end", style: { marginTop: '10px' } }, { children: _jsxs(Button, __assign({ type: type || "button", variant: "outlined", color: "primary", onClick: onClickHandler, style: { textTransform: "none" } }, { children: [labelPrefix, " ", label] })) })) })) }));
};
export default FormButton;
