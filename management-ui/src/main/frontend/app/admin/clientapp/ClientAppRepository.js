export function findAllClientApplications() {
    return fetch("/secure/api/client-applications",
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

export function findClientApplicationFor(clientAppId) {
    return fetch(`/secure/api/client-applications/${clientAppId}`,
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

export function saveClientApplicationFor(clientAppId, clientApp) {
    return fetch(`/secure/api/client-applications/${clientAppId}`,
        {
            method: "PUT",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(clientApp),
            credentials: 'same-origin'
        })
}

export function resetSecretFor(clientAppId, secret) {
    return fetch(`/secure/api/client-applications/${clientAppId}`,
        {
            method: "PATCH",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({"secret": secret}),
            credentials: 'same-origin'
        })
}

export function deleteClientApplicationFor(clientAppId) {
    return fetch(`/secure/api/client-applications/${clientAppId}`,
        {
            method: "DELETE",
            credentials: 'same-origin'
        })
}