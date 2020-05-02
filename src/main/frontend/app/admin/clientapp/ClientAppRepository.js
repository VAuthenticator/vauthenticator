export function findAllClientApplications() {
    return fetch("/vauthenticator/api/client-applications",
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
    return fetch(`/vauthenticator/api/client-applications/${clientAppId}`,
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