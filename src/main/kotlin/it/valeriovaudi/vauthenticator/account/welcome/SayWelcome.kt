package it.valeriovaudi.vauthenticator.account.welcome

import it.valeriovaudi.vauthenticator.account.AccountNotFoundException
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.mail.MailSenderService

class SayWelcome(
    private val accountRepository: AccountRepository,
    private val welcomeMailSender: MailSenderService
) {

    fun welcome(mail: String) =
        accountRepository.accountFor(mail)
            .map { welcomeMailSender.sendFor(it) }
            .orElseThrow {AccountNotFoundException("no account with mail $mail in the database")}

}