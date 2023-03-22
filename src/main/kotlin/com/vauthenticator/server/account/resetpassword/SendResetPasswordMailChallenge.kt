package com.vauthenticator.server.account.resetpassword

import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.account.tiket.VerificationTicketFactory
import com.vauthenticator.server.extentions.hasEnoughScopes
import com.vauthenticator.server.mail.MailSenderService
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import com.vauthenticator.server.oauth2.clientapp.ClientApplicationRepository
import com.vauthenticator.server.oauth2.clientapp.InsufficientClientApplicationScopeException
import com.vauthenticator.server.oauth2.clientapp.Scope

class SendResetPasswordMailChallenge(
    private val clientApplicationRepository: ClientApplicationRepository,
    private val accountRepository: AccountRepository,
    private val ticketFactory: VerificationTicketFactory,
    private val mailSenderService: MailSenderService,
    private val frontChannelBaseUrl: String
) {

    fun sendResetPasswordMail(mail: String, clientAppId: ClientAppId) {
        clientApplicationRepository.findOne(clientAppId)
            .map {
                if (it.hasEnoughScopes(Scope.RESET_PASSWORD)) {
                    sendResetPasswordMailFor(mail, clientAppId)
                } else {
                    throw InsufficientClientApplicationScopeException("The client app ${clientAppId.content} does not support reset-password use case........ consider to add ${Scope.RESET_PASSWORD.content} as scope")
                }
            }
    }

    private fun sendResetPasswordMailFor(mail: String, clientAppId: ClientAppId) {
        accountRepository.accountFor(username = mail)
            .map {
                val ticket = ticketFactory.createTicketFor(it, clientAppId)
                mailSenderService.sendFor(
                    account = it,
                    mapOf("resetPasswordLink" to "$frontChannelBaseUrl/reset-password/${ticket.content}")
                )
            }
    }

}
