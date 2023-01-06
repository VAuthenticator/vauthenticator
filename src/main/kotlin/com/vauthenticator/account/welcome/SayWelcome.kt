package com.vauthenticator.account.welcome

import com.vauthenticator.account.AccountNotFoundException
import com.vauthenticator.account.repository.AccountRepository
import com.vauthenticator.mail.MailSenderService

class SayWelcome(
    private val accountRepository: AccountRepository,
    private val welcomeMailSender: MailSenderService
) {

    fun welcome(mail: String) =
        accountRepository.accountFor(mail)
            .map { welcomeMailSender.sendFor(it) }
            .orElseThrow { AccountNotFoundException("no account with mail $mail in the database") }

}