package com.vauthenticator.server.config

import com.hubspot.jinjava.Jinjava
import com.vauthenticator.server.account.mailverification.SendVerifyMailChallenge
import com.vauthenticator.server.account.mailverification.VerifyMailChallengeSent
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.account.tiket.TicketRepository
import com.vauthenticator.server.account.tiket.VerificationTicketFactory
import com.vauthenticator.server.document.DocumentRepository
import com.vauthenticator.server.mail.*
import com.vauthenticator.server.mfa.MfaMethodsEnrolmentAssociation
import com.vauthenticator.server.oauth2.clientapp.ClientApplicationRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender

@Configuration(proxyBeanMethods = false)
class MailVerificationConfig {

    @Bean
    fun sendVerifyMailChallenge(clientAccountRepository: ClientApplicationRepository,
                                accountRepository: AccountRepository,
                                verificationTicketFactory: VerificationTicketFactory,
                                verificationMailSender: MailSenderService,
                                @Value("\${vauthenticator.host}") frontChannelBaseUrl: String) =
            SendVerifyMailChallenge(
                accountRepository,
                verificationTicketFactory,
                verificationMailSender,
                frontChannelBaseUrl
            )

    @Bean
    fun verifyMailChallengeSent(accountRepository: AccountRepository,
                                ticketRepository: TicketRepository,
                                mfaMethodsEnrolmentAssociation : MfaMethodsEnrolmentAssociation
    ) =
            VerifyMailChallengeSent(
                accountRepository,
                ticketRepository,
                mfaMethodsEnrolmentAssociation
            )

    @Bean
    fun verificationMailSender(javaMailSender: JavaMailSender, documentRepository: DocumentRepository, noReplyMailConfiguration: NoReplyMailConfiguration) =
            JavaMailSenderService(documentRepository,
                    javaMailSender,
                    JinjavaMailTemplateResolver(Jinjava()),
                    SimpleMailMessageFactory(noReplyMailConfiguration.from, noReplyMailConfiguration.welcomeMailSubject, MailType.EMAIL_VERIFICATION)
            )
}