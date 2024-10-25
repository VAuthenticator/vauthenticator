package com.vauthenticator.server.password.resetpassword

import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.communication.domain.EMailSenderService
import com.vauthenticator.server.oauth2.clientapp.domain.ClientAppId
import com.vauthenticator.server.ticket.domain.TicketCreator

class SendResetPasswordMailChallenge(
    private val accountRepository: AccountRepository,
    private val ticketFactory: TicketCreator,
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
