export function signUp() {
    return fetch("/vauthenticator/api/accounts",
        {
            method: "POST",
            headers: {
                'Accept': 'application/json'
            },
            credentials: 'same-origin'
        })
}