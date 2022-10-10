package it.valeriovaudi.vauthenticator.account.resetpassword

import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.account.tiket.VerificationTicketFactory
import it.valeriovaudi.vauthenticator.mail.MailSenderService
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppId

class SendResetPasswordMailChallenge(private val accountRepository: AccountRepository,
                                     private val ticketFactory: VerificationTicketFactory,
                                     private val mailSenderService: MailSenderService,
                                     private val frontChannelBaseUrl: String) {

    fun sendResetPasswordMail(mail: String) {
        accountRepository.accountFor(username = mail)
                .map {
                    val ticket = ticketFactory.createTicketFor(it, ClientAppId.empty())
                    mailSenderService.sendFor(account = it, mapOf("resetPasswordLink" to "$frontChannelBaseUrl/reset-password/${ticket.content}"))
                }
    }

}
