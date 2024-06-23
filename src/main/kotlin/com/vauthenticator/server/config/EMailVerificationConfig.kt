package com.vauthenticator.server.config

import com.hubspot.jinjava.Jinjava
import com.vauthenticator.document.repository.DocumentRepository
import com.vauthenticator.server.account.emailverification.SendVerifyEMailChallenge
import com.vauthenticator.server.account.emailverification.SendVerifyEMailChallengeUponSignUpEventConsumer
import com.vauthenticator.server.account.emailverification.VerifyEMailChallenge
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.account.ticket.TicketRepository
import com.vauthenticator.server.account.ticket.VerificationTicketFactory
import com.vauthenticator.server.email.*
import com.vauthenticator.server.mfa.domain.MfaMethodsEnrolmentAssociation
import com.vauthenticator.server.oauth2.clientapp.ClientApplicationRepository
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
        verificationTicketFactory: VerificationTicketFactory,
        verificationMailSender: EMailSenderService,
        @Value("\${vauthenticator.host}") frontChannelBaseUrl: String
    ) =
        SendVerifyEMailChallenge(
            accountRepository,
            verificationTicketFactory,
            verificationMailSender,
            frontChannelBaseUrl
        )

    @Bean
    fun verifyMailChallengeSent(
        accountRepository: AccountRepository,
        ticketRepository: TicketRepository,
        mfaMethodsEnrolmentAssociation: MfaMethodsEnrolmentAssociation
    ) =
        VerifyEMailChallenge(
            accountRepository,
            ticketRepository,
            mfaMethodsEnrolmentAssociation
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
                noReplyEMailConfiguration.welcomeMailSubject,
                EMailType.EMAIL_VERIFICATION
            )
        )

    @Bean
    fun sendVerifyMailChallengeUponSignUpEventConsumer(mailChallenge: SendVerifyEMailChallenge) =
        SendVerifyEMailChallengeUponSignUpEventConsumer(mailChallenge)
}