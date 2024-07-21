package com.vauthenticator.server.account.emailverification

import com.vauthenticator.server.account.AccountNotFoundException
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.email.EMailSenderService
import com.vauthenticator.server.mfa.domain.MfaMethod
import com.vauthenticator.server.mfa.domain.MfaMethodsEnrollment
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import com.vauthenticator.server.ticket.TicketId
import org.slf4j.LoggerFactory

private const val LINK_KEY = "verificationEMailLink"

class SendVerifyEMailChallenge(
    private val accountRepository: AccountRepository,
    private val mfaMethodsEnrollment: MfaMethodsEnrollment,
    private val mailVerificationMailSender: EMailSenderService,
    private val frontChannelBaseUrl: String
) {

    private val logger = LoggerFactory.getLogger(SendVerifyEMailChallenge::class.java)

    fun sendVerifyMail(email: String) {
        accountRepository.accountFor(email)
            .map { account ->
                val verificationTicket =
                    mfaMethodsEnrollment.enroll(
                        account,
                        MfaMethod.EMAIL_MFA_METHOD,
                        account.email,
                        ClientAppId.empty(),
                        false
                    )
                val mailContext = mailContextFrom(verificationTicket)
                mailVerificationMailSender.sendFor(account, mailContext)
            }.orElseThrow {
                logger.warn("account not found")
                AccountNotFoundException("account not found")
            }
    }

    private fun mailContextFrom(ticketId: TicketId): Map<String, String> {
        val verificationLink = "$frontChannelBaseUrl/email-verify/${ticketId.content}"
        return mapOf(LINK_KEY to verificationLink)
    }

}