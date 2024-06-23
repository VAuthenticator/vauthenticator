package com.vauthenticator.server.account.welcome

import com.vauthenticator.server.account.AccountNotFoundException
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.email.EMailSenderService

class SayWelcome(
    private val accountRepository: AccountRepository,
    private val welcomeMailSender: EMailSenderService
) {

    fun welcome(email: String): Unit =
        accountRepository.accountFor(email)
            .map { welcomeMailSender.sendFor(it, emptyMap()) }
            .orElseThrow { AccountNotFoundException("no account with email $email in the database") }

}