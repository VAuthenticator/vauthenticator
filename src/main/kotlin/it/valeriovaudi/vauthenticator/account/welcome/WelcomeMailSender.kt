package it.valeriovaudi.vauthenticator.account.welcome

import it.valeriovaudi.vauthenticator.account.Account
import it.valeriovaudi.vauthenticator.account.signup.SignUpConfirmationMailConfiguration
import it.valeriovaudi.vauthenticator.mail.MailContextFactory
import it.valeriovaudi.vauthenticator.mail.MailMessage
import it.valeriovaudi.vauthenticator.mail.MailSenderService
import it.valeriovaudi.vauthenticator.mail.MailType

open class WelcomeMailSender(
        private val mailSenderService: MailSenderService,
        private val mailContextFactory: MailContextFactory,
        private val mailConfiguration: SignUpConfirmationMailConfiguration) {

    open fun sendFor(account: Account) {
        mailSenderService.send(MailMessage(account.email,
                mailConfiguration.from,
                mailConfiguration.subject,
                MailType.WELCOME,
                mailContextFactory.apply(account, emptyMap())
        ))
    }
}