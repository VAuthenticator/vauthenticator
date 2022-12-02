package it.valeriovaudi.vauthenticator.mfa

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.CredentialsContainer

class MfaAuthentication(val delegate: Authentication) :
    AbstractAuthenticationToken(emptyList()) {

    override fun getPrincipal(): Any {
        return delegate.principal
    }

    override fun getCredentials(): Any {
        return delegate.credentials
    }

    override fun eraseCredentials() {
        if (delegate is CredentialsContainer) {
            (delegate as CredentialsContainer).eraseCredentials()
        }
    }

    override fun isAuthenticated(): Boolean {
        return false
    }
}
