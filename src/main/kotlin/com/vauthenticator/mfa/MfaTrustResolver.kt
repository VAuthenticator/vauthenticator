package com.vauthenticator.mfa

import org.springframework.security.authentication.AuthenticationTrustResolver
import org.springframework.security.authentication.AuthenticationTrustResolverImpl
import org.springframework.security.core.Authentication

class MfaTrustResolver : AuthenticationTrustResolver {
    private val delegate: AuthenticationTrustResolver = AuthenticationTrustResolverImpl()
    override fun isAnonymous(authentication: Authentication): Boolean {
        return delegate.isAnonymous(authentication) || authentication is MfaAuthentication
    }

    override fun isRememberMe(authentication: Authentication): Boolean {
        return delegate.isRememberMe(authentication)
    }
}
