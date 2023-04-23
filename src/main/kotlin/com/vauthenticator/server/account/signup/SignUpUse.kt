package com.vauthenticator.server.account.signup

import com.vauthenticator.server.account.Account
import com.vauthenticator.server.account.mailverification.SendVerifyMailChallenge
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.account.welcome.SayWelcome
import com.vauthenticator.server.extentions.hasEnoughScopes
import com.vauthenticator.server.oauth2.clientapp.*
import com.vauthenticator.server.password.PasswordPolicy
import com.vauthenticator.server.password.VAuthenticatorPasswordEncoder

open class SignUpUse(
    private val passwordPolicy: PasswordPolicy,
    private val clientAccountRepository: ClientApplicationRepository,
    private val accountRepository: AccountRepository,
    private val sendVerifyMailChallenge: SendVerifyMailChallenge,
    private val vAuthenticatorPasswordEncoder: VAuthenticatorPasswordEncoder,
    private val sayWelcome: SayWelcome
) {
    open fun execute(clientAppId: ClientAppId, account: Account) {
        passwordPolicy.accept(account.password)
        clientAccountRepository.findOne(clientAppId)
            .map {
                if (it.hasEnoughScopes(Scopes.from(Scope.SIGN_UP, Scope.MAIL_VERIFY))) {
                    val registeredAccount = account.copy(
                        authorities = it.authorities.content.map { it.content },
                        password = vAuthenticatorPasswordEncoder.encode(account.password)
                    )
                    accountRepository.create(registeredAccount)
                    sayWelcome.welcome(registeredAccount.email)
                    sendVerifyMailChallenge.sendVerifyMail(account.email)
                } else {
                    throw InsufficientClientApplicationScopeException("The client app ${clientAppId.content} does not support signup use case........ consider to add ${Scope.SIGN_UP.content} as scope")
                }

            }
    }
}