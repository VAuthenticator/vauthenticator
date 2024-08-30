package com.vauthenticator.server.config

import com.hubspot.jinjava.Jinjava
import com.vauthenticator.document.repository.DocumentRepository
import com.vauthenticator.server.account.emailverification.SendVerifyEMailChallenge
import com.vauthenticator.server.account.emailverification.SendVerifyEMailChallengeUponSignUpEventConsumer
import com.vauthenticator.server.account.emailverification.VerifyEMailChallenge
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.communication.email.*
import com.vauthenticator.server.mfa.domain.MfaMethodsEnrollment
import com.vauthenticator.server.mfa.domain.MfaMethodsEnrollmentAssociation
import com.vauthenticator.server.oauth2.clientapp.domain.ClientApplicationRepository
import com.vauthenticator.server.ticket.TicketRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender

@Configuration(proxyBeanMethods = false)
class EMailVerificationConfig {

    @Bean
    fun sendVerifyMailChallenge(
        clientAccountRepository: ClientApplicationRepository,
        accountRepository: AccountRepository,
        mfaMethodsEnrollment: MfaMethodsEnrollment,
        verificationMailSender: EMailSenderService,
        @Value("\${vauthenticator.host}") frontChannelBaseUrl: String
    ) =
        SendVerifyEMailChallenge(
            accountRepository,
            mfaMethodsEnrollment,
            verificationMailSender,
            frontChannelBaseUrl
        )

    @Bean
    fun verifyMailChallengeSent(
        accountRepository: AccountRepository,
        ticketRepository: TicketRepository,
        mfaMethodsEnrollmentAssociation: MfaMethodsEnrollmentAssociation
    ) =
        VerifyEMailChallenge(
            ticketRepository,
            accountRepository,
            mfaMethodsEnrollmentAssociation
        )

    @Bean
    fun verificationMailSender(
        javaMailSender: JavaMailSender,
        documentRepository: DocumentRepository,
        noReplyEMailConfiguration: NoReplyEMailConfiguration
    ) =
        JavaEMailSenderService(
            documentRepository,
            javaMailSender,
            JinjavaMailTemplateResolver(Jinjava()),
            SimpleEMailMessageFactory(
                noReplyEMailConfiguration.from,
                noReplyEMailConfiguration.welcomeEMailSubject,
                EMailType.EMAIL_VERIFICATION
            )
        )

    @Bean
    fun sendVerifyMailChallengeUponSignUpEventConsumer(mailChallenge: SendVerifyEMailChallenge) =
        SendVerifyEMailChallengeUponSignUpEventConsumer(mailChallenge)
}