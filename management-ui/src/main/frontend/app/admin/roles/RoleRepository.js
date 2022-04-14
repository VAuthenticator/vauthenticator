export function findAllRoles() {
    return fetch("/secure/api/roles",
        {
            method: "GET",
            headers: {
                'Accept': 'application/json'
            },
            credentials: 'same-origin'
        }).then(response => {
        return response.json()
    })
}

export function deleteRoleFor(roleId) {
    return fetch(`/secure/api/roles/${roleId}`,
        {
            method: "DELETE",
            credentials: 'same-origin'
        })
}

export function saveRoleFor(role) {
    return fetch("/secure/api/roles",
        {
            method: "PUT",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(role),
            credentials: 'same-origin'
        })
}
