package com.vauthenticator.server.password.changepassword

import com.vauthenticator.server.account.AccountNotFoundException
import com.vauthenticator.server.account.Email
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.events.ChangePasswordEvent
import com.vauthenticator.server.events.VAuthenticatorEventsDispatcher
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import com.vauthenticator.server.password.Password
import com.vauthenticator.server.password.PasswordPolicy
import com.vauthenticator.server.password.VAuthenticatorPasswordEncoder
import org.springframework.security.authentication.event.AbstractAuthenticationEvent
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import java.security.Principal
import java.time.Instant

class ChangePassword(
    private val eventsDispatcher: VAuthenticatorEventsDispatcher,
    private val passwordPolicy: PasswordPolicy,
    private val passwordEncoder: VAuthenticatorPasswordEncoder,
    private val accountRepository: AccountRepository
) {
    fun resetPasswordFor(principal: Principal, request: ChangePasswordRequest) {
        passwordPolicy.accept(principal.name, request.newPassword)
        accountRepository.accountFor(principal.name)
            .map { account ->
                val newEncodedPassword = passwordEncoder.encode(request.newPassword)
                val updatedAccount = account.copy(password = newEncodedPassword)
                accountRepository.save(updatedAccount)
                eventsDispatcher.dispatch(
                    ChangePasswordEvent(
                        Email(principal.name),
                        ClientAppId.empty(),
                        Instant.now(),
                        Password(newEncodedPassword)
                    )
                )
            }.orElseThrow { AccountNotFoundException("account not found") }
    }
}

class ChangePasswordFailureEvent(authentication: Authentication, exception: AuthenticationException) :
    AbstractAuthenticationFailureEvent(authentication, exception) {}

class ChangePasswordSuccessEvent(authentication: Authentication) : AbstractAuthenticationEvent(authentication) {}


data class ChangePasswordRequest(val newPassword: String)

class ChangePasswordException(message: String) : AuthenticationException(message)