export const authorizedGrantTypesParam = (authorizedGrantTypes) => {
    return Object.keys(authorizedGrantTypes)
        .map((key, index) => {
            return {[key]: authorizedGrantTypes[key]};
        }).filter(obj => {
            return Object.values(obj)[0] === true
        })
        .map((obj) => {
            return Object.keys(obj)[0]
        })
}
export const authorizedGrantTypesRegistry = (authorizedGrantTypes) => {
    let registry = {
        authorization_code: false,
        refresh_token: false,
        client_credentials: false,
        password: false,
        implicit: false
    }

    if (authorizedGrantTypes) {
        authorizedGrantTypes.map(authorizedGrantType => {
            registry[authorizedGrantType] = true;
        })
    }

    return registry;
}