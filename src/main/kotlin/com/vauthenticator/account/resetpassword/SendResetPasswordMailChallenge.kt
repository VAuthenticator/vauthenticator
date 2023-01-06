package com.vauthenticator.account.resetpassword

import com.vauthenticator.account.repository.AccountRepository
import com.vauthenticator.account.tiket.VerificationTicketFactory
import com.vauthenticator.mail.MailSenderService
import com.vauthenticator.oauth2.clientapp.*

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
                if (hasEnoughScopes(it)) {
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

    private fun hasEnoughScopes(it: ClientApplication) =
        it.scopes.content.contains(Scope.RESET_PASSWORD)

}
