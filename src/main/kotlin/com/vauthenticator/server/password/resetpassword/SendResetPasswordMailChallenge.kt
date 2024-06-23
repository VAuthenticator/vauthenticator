package com.vauthenticator.server.password.resetpassword

import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.account.ticket.VerificationTicketFactory
import com.vauthenticator.server.email.EMailSenderService
import com.vauthenticator.server.oauth2.clientapp.ClientAppId

class SendResetPasswordMailChallenge(
    private val accountRepository: AccountRepository,
    private val ticketFactory: VerificationTicketFactory,
    private val emailSenderService: EMailSenderService,
    private val frontChannelBaseUrl: String
) {

    fun sendResetPasswordMailFor(email: String) {
        accountRepository.accountFor(username = email)
            .map {
                val ticket = ticketFactory.createTicketFor(it, ClientAppId.empty())
                emailSenderService.sendFor(
                    account = it,
                    mapOf("resetPasswordLink" to "$frontChannelBaseUrl/reset-password/${ticket.content}")
                )
            }
    }

}
