package com.vauthenticator.server.account.signup

import com.vauthenticator.server.account.Account
import com.vauthenticator.server.account.mailverification.SendVerifyMailChallenge
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.account.welcome.SayWelcome
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import com.vauthenticator.server.oauth2.clientapp.ClientApplicationRepository
import com.vauthenticator.server.oauth2.clientapp.InsufficientClientApplicationScopeException
import com.vauthenticator.server.oauth2.clientapp.Scope
import com.vauthenticator.server.password.VAuthenticatorPasswordEncoder

open class SignUpUseCase(
    private val clientAccountRepository: ClientApplicationRepository,
    private val accountRepository: AccountRepository,
    private val sendVerifyMailChallenge: SendVerifyMailChallenge,
    private val vAuthenticatorPasswordEncoder: VAuthenticatorPasswordEncoder,
    private val  ayWelcome: SayWelcome
) {
    open fun execute(clientAppId: ClientAppId, account: Account) {
        clientAccountRepository.findOne(clientAppId)
                .map {
                    if(hasScopes(it.scopes.content)) {
                        val registeredAccount = account.copy(
                                authorities = it.authorities.content.map { it.content },
                                password = vAuthenticatorPasswordEncoder.encode(account.password))
                        accountRepository.create(registeredAccount)
                        ayWelcome.welcome(registeredAccount.email)
                        sendVerifyMailChallenge.sendVerifyMail(account.email, clientAppId)
                    } else {
                        throw InsufficientClientApplicationScopeException("The client app ${clientAppId.content} does not support signup use case........ consider to add ${Scope.SIGN_UP.content} as scope")
                    }

                }
    }

    private fun hasScopes(scope : Set<Scope>) = scope.stream().anyMatch(::hasScope)
    private fun hasScope(scope : Scope) = setOf(Scope.SIGN_UP, Scope.MAIL_VERIFY).contains(scope)
}

