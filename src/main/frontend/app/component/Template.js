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
import { Container, Paper } from "@mui/material";
var Template = function (_a) {
    var maxWidth = _a.maxWidth, children = _a.children;
    return (_jsx(Container, __assign({ maxWidth: maxWidth }, { children: _jsx(Paper, __assign({ elevation: 3 }, { children: children })) })));
};
export default Template;
