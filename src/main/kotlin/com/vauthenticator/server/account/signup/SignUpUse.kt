package com.vauthenticator.server.account.signup

import com.vauthenticator.server.account.Account
import com.vauthenticator.server.account.mailverification.SendVerifyMailChallenge
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.account.welcome.SayWelcome
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import com.vauthenticator.server.oauth2.clientapp.ClientApplicationRepository
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
                val registeredAccount = account.copy(
                    authorities = it.authorities.content.map { it.content }.toSet(),
                    password = vAuthenticatorPasswordEncoder.encode(account.password)
                )
                accountRepository.create(registeredAccount)
                sayWelcome.welcome(registeredAccount.email)
                sendVerifyMailChallenge.sendVerifyMail(account.email)
            }
    }
}