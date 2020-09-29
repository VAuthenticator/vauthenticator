export function findAccountFor(email) {
    return fetch(`/vauthenticator/api/accounts/${email}/email`,
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

export function findAllAccounts() {
    return fetch("/vauthenticator/api/accounts",
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

export function saveAccountFor(account) {
    return fetch(`/vauthenticator/api/accounts/${account.email}/email`,
        {
            method: "PUT",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(account),
            credentials: 'same-origin'
        })
}
