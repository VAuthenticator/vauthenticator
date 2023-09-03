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
import { Grid, TextField } from "@mui/material";
var FormInputTextField = function (_a) {
    var id = _a.id, label = _a.label, type = _a.type, required = _a.required, autoFocus = _a.autoFocus, disabled = _a.disabled, suffix = _a.suffix, value = _a.value, handler = _a.handler;
    return _jsxs(Grid, __assign({ container: true, spacing: 8, alignItems: "flex-end" }, { children: [suffix && _jsx(Grid, __assign({ item: true }, { children: suffix })), _jsx(Grid, __assign({ item: true, md: true, sm: true, xs: true }, { children: _jsx(TextField, { name: id, id: id, label: label, type: type || "text", disabled: disabled, variant: "outlined", fullWidth: true, autoFocus: autoFocus, required: required || false, value: value, onChange: handler }) }))] }));
};
export default FormInputTextField;
