export function findAccountFor(email) {
    return fetch(`/secure/api/accounts/${email}/email`,
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
    return fetch("/secure/api/accounts",
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
    return fetch(`/secure/api/accounts/${account.email}/email`,
        {
            method: "PUT",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(account),
            credentials: 'same-origin'
        })
}
