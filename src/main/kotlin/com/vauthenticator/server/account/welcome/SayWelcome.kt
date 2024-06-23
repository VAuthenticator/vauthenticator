package com.vauthenticator.server.account.welcome

import com.vauthenticator.server.account.AccountNotFoundException
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.email.MailSenderService

class SayWelcome(
    private val accountRepository: AccountRepository,
    private val welcomeMailSender: MailSenderService
) {

    fun welcome(email: String): Unit =
        accountRepository.accountFor(email)
            .map { welcomeMailSender.sendFor(it) }
            .orElseThrow { AccountNotFoundException("no account with email $mail in the database") }

}