package com.vauthenticator.server.account.welcome

import com.vauthenticator.server.account.AccountNotFoundException
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.mail.MailSenderService

class SayWelcome(
    private val accountRepository: AccountRepository,
    private val welcomeMailSender: MailSenderService
) {

    fun welcome(mail: String) =
        accountRepository.accountFor(mail)
            .map { welcomeMailSender.sendFor(it) }
            .orElseThrow { AccountNotFoundException("no account with mail $mail in the database") }

}