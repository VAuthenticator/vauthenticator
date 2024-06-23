package com.vauthenticator.server.config

import com.hubspot.jinjava.Jinjava
import com.vauthenticator.document.repository.DocumentRepository
import com.vauthenticator.server.account.mailverification.SendVerifyMailChallenge
import com.vauthenticator.server.account.mailverification.SendVerifyMailChallengeUponSignUpEventConsumer
import com.vauthenticator.server.account.mailverification.VerifyMailChallenge
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.account.ticket.TicketRepository
import com.vauthenticator.server.account.ticket.VerificationTicketFactory
import com.vauthenticator.server.email.*
import com.vauthenticator.server.mfa.domain.MfaMethodsEnrolmentAssociation
import com.vauthenticator.server.oauth2.clientapp.ClientApplicationRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.email.javamail.JavaMailSender

@Configuration(proxyBeanMethods = false)
class EMailVerificationConfig {

    @Bean
    fun sendVerifyMailChallenge(
        clientAccountRepository: ClientApplicationRepository,
        accountRepository: AccountRepository,
        verificationTicketFactory: VerificationTicketFactory,
        verificationMailSender: MailSenderService,
        @Value("\${vauthenticator.host}") frontChannelBaseUrl: String
    ) =
        SendVerifyMailChallenge(
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
        VerifyMailChallenge(
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
        JavaMailSenderService(
            documentRepository,
            javaMailSender,
            JinjavaMailTemplateResolver(Jinjava()),
            SimpleMailMessageFactory(
                noReplyEMailConfiguration.from,
                noReplyEMailConfiguration.welcomeMailSubject,
                MailType.EMAIL_VERIFICATION
            )
        )

    @Bean
    fun sendVerifyMailChallengeUponSignUpEventConsumer(mailChallenge: SendVerifyMailChallenge) =
        SendVerifyMailChallengeUponSignUpEventConsumer(mailChallenge)
}