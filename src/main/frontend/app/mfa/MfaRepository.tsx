export type MfaAccountEnrolledMethod = {
    userName: string
    mfaMethod: string
    mfaChannel: string
}

export async function sendMfaCode(): Promise<void> {
    await fetch("/api/mfa/challenge", {
        method: 'PUT', // *GET, POST, PUT, DELETE, etc.
        credentials: 'same-origin', // include, *same-origin, omit
    });
    return new Promise(resolve => resolve());
}

export async function getMfaMethods(): Promise<MfaAccountEnrolledMethod[]> {
    let response = await fetch("/api/mfa/enrollment", {
        method: 'GET', // *GET, POST, PUT, DELETE, etc.
        credentials: 'same-origin', // include, *same-origin, omit
    });
    return await response.json() as MfaAccountEnrolledMethod[];
}