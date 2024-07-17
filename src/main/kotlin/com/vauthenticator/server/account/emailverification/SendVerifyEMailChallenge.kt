package com.vauthenticator.server.account.emailverification

import com.vauthenticator.server.account.AccountNotFoundException
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.email.EMailSenderService
import com.vauthenticator.server.mfa.domain.VerificationTicket
import com.vauthenticator.server.mfa.domain.VerificationTicketFactory
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import org.slf4j.LoggerFactory

private const val LINK_KEY = "verificationEMailLink"

class SendVerifyEMailChallenge(
    private val accountRepository: AccountRepository,
    private val verificationTicketFactory: VerificationTicketFactory,
    private val mailVerificationMailSender: EMailSenderService,
    private val frontChannelBaseUrl: String
) {

    private val logger = LoggerFactory.getLogger(SendVerifyEMailChallenge::class.java)

    fun sendVerifyMail(email: String) {
        accountRepository.accountFor(email)
            .map { account ->
                val verificationTicket = verificationTicketFactory.createTicketFor(account, ClientAppId.empty())
                val mailContext = mailContextFrom(verificationTicket)
                mailVerificationMailSender.sendFor(account, mailContext)
            }.orElseThrow {
                logger.warn("account not found")
                AccountNotFoundException("account not found")
            }
    }

    private fun mailContextFrom(verificationTicket: VerificationTicket): Map<String, String> {
        val verificationLink = "$frontChannelBaseUrl/email-verify/${verificationTicket.content}"
        return mapOf(LINK_KEY to verificationLink)
    }

}