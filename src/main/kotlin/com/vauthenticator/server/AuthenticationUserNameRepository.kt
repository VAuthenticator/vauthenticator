package com.vauthenticator.server

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class AuthenticationUserNameRepository {
    fun getCurrentAuthenticatedUserName() = SecurityContextHolder.getContext().authentication.name!!

}