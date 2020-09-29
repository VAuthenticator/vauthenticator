import FormControlLabel from "@material-ui/core/FormControlLabel";
import Checkbox from "@material-ui/core/Checkbox";
import React from "react";
import StickyHeadTable from "./StickyHeadTable";

export const drawAuthorityRows = (setAuthorityRows, setAuthorities, authoritiesValues, roleValues) => roleValues.map(role => {
    return {
        name: role.name,
        description: role.description,
        delete: <FormControlLabel control={
            <Checkbox onChange={() => {
                const roleIndex = authoritiesValues.indexOf(role.name)

                if (roleIndex !== -1) {
                    authoritiesValues.splice(roleIndex, 1)
                } else {
                    authoritiesValues.push(role.name)
                }

                setAuthorities(authoritiesValues)
                setAuthorityRows(drawAuthorityRows(setAuthorityRows, setAuthorities, authoritiesValues, roleValues))
            }}
                      checked={authoritiesValues.indexOf(role.name) !== -1}/>
        }/>
    }
})


export default ({columns, authorityRows}) => {
    return <StickyHeadTable columns={columns} rows={authorityRows}/>
}