package com.vauthenticator.server

import org.springframework.security.core.context.SecurityContextHolder

class AuthenticationUserNameRepository {
    fun getCurrentAuthenticatedUserName() = SecurityContextHolder.getContext().authentication.name!!

}