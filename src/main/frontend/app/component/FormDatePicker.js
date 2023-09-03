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
import { Grid } from "@mui/material";
import { DesktopDatePicker } from "@mui/x-date-pickers";
import { AdapterMoment } from '@mui/x-date-pickers/AdapterMoment';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import moment from 'moment';
var FormDatePicker = function (_a) {
    var label = _a.label, value = _a.value, onClickHandler = _a.onClickHandler, pattern = _a.pattern;
    var val = value && moment(value, pattern);
    console.log("val: " + val);
    return _jsx(Grid, __assign({ container: true, alignItems: "flex-end" }, { children: _jsx(Grid, __assign({ item: true, md: true, sm: true, xs: true }, { children: _jsx(LocalizationProvider, __assign({ dateAdapter: AdapterMoment }, { children: _jsx(DesktopDatePicker, { label: label, format: FormDateFormatPattern, onChange: onClickHandler || {}, value: val }) })) })) }));
};
export var FormDateFormatPattern = 'DD/MM/YYYY';
export var ApiDateFormatPattern = 'YYYY-MM-DD';
export default FormDatePicker;
