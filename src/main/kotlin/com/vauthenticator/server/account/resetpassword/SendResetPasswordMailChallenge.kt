package com.vauthenticator.server.account.resetpassword

import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.account.tiket.VerificationTicketFactory
import com.vauthenticator.server.mail.MailSenderService
import com.vauthenticator.server.oauth2.clientapp.ClientAppId

class SendResetPasswordMailChallenge(
    private val accountRepository: AccountRepository,
    private val ticketFactory: VerificationTicketFactory,
    private val mailSenderService: MailSenderService,
    private val frontChannelBaseUrl: String
) {

    fun sendResetPasswordMailFor(mail: String) {
        accountRepository.accountFor(username = mail)
            .map {
                val ticket = ticketFactory.createTicketFor(it, ClientAppId.empty())
                mailSenderService.sendFor(
                    account = it,
                    mapOf("resetPasswordLink" to "$frontChannelBaseUrl/reset-password/${ticket.content}")
                )
            }
    }

}
