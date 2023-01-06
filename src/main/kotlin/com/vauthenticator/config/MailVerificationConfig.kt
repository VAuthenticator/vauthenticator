package com.vauthenticator.config

import com.hubspot.jinjava.Jinjava
import com.vauthenticator.account.mailverification.SendVerifyMailChallenge
import com.vauthenticator.account.mailverification.VerifyMailChallengeSent
import com.vauthenticator.account.repository.AccountRepository
import com.vauthenticator.account.tiket.TicketRepository
import com.vauthenticator.account.tiket.VerificationTicketFactory
import com.vauthenticator.document.DocumentRepository
import com.vauthenticator.mail.*
import com.vauthenticator.mfa.MfaMethodsEnrolmentAssociation
import com.vauthenticator.oauth2.clientapp.ClientApplicationRepository
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
            SendVerifyMailChallenge(clientAccountRepository,
                    accountRepository,
                    verificationTicketFactory,
                    verificationMailSender,
                    frontChannelBaseUrl)

    @Bean
    fun verifyMailChallengeSent(clientAccountRepository: ClientApplicationRepository,
                                accountRepository: AccountRepository,
                                ticketRepository: TicketRepository,
                                mfaMethodsEnrolmentAssociation : MfaMethodsEnrolmentAssociation
    ) =
            VerifyMailChallengeSent(
                clientAccountRepository,
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