package com.vauthenticator.server.account.signup

import com.vauthenticator.server.account.Account
import com.vauthenticator.server.account.Email
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.events.SignUpEvent
import com.vauthenticator.server.events.VAuthenticatorEventsDispatcher
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import com.vauthenticator.server.oauth2.clientapp.ClientApplicationRepository
import com.vauthenticator.server.password.Password
import com.vauthenticator.server.password.PasswordPolicy
import com.vauthenticator.server.password.VAuthenticatorPasswordEncoder
import java.time.Instant

open class SignUpUse(
    private val passwordPolicy: PasswordPolicy,
    private val clientAccountRepository: ClientApplicationRepository,
    private val accountRepository: AccountRepository,
    private val vAuthenticatorPasswordEncoder: VAuthenticatorPasswordEncoder,
    private val eventsDispatcher: VAuthenticatorEventsDispatcher
) {
    open fun execute(clientAppId: ClientAppId, account: Account) {
        passwordPolicy.accept(account.email, account.password)
        clientAccountRepository.findOne(clientAppId)
            .map {
                val encodedPassword = vAuthenticatorPasswordEncoder.encode(account.password)
                val registeredAccount = account.copy(
                    authorities = it.authorities.content.map { it.content }.toSet(),
                    password = encodedPassword
                )
                accountRepository.create(registeredAccount)

                eventsDispatcher.dispatch(
                    SignUpEvent(Email(account.email), clientAppId, Instant.now(), Password(encodedPassword))
                )
            }
    }
}