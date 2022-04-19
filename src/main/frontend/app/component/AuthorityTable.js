import FormControlLabel from "@material-ui/core/FormControlLabel";
import Checkbox from "@material-ui/core/Checkbox";
import React from "react";
import StickyHeadTable from "./StickyHeadTable";

export const drawAuthorityRows = (setAuthorityRows, setAuthorities, AclientAppauthorities, allRole) => allRole.map(role => {
    return {
        name: role.name,
        description: role.description,
        delete: <FormControlLabel control={
            <Checkbox onChange={() => {
                const roleIndex = AclientAppauthorities.indexOf(role.name)

                if (roleIndex !== -1) {
                    AclientAppauthorities.splice(roleIndex, 1)
                } else {
                    AclientAppauthorities.push(role.name)
                }

                setAuthorities(AclientAppauthorities)
                setAuthorityRows(drawAuthorityRows(setAuthorityRows, setAuthorities, AclientAppauthorities, allRole))
            }}
                      checked={AclientAppauthorities.indexOf(role.name) !== -1}/>
        }/>
    }
})


export default ({columns, authorityRows}) => {
    return <StickyHeadTable columns={columns} rows={authorityRows}/>
}