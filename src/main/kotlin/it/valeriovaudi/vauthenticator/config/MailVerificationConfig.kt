package it.valeriovaudi.vauthenticator.config

import com.hubspot.jinjava.Jinjava
import it.valeriovaudi.vauthenticator.account.mailverification.SendVerifyMailChallenge
import it.valeriovaudi.vauthenticator.account.mailverification.VerifyMailChallengeSent
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.account.tiket.TicketRepository
import it.valeriovaudi.vauthenticator.account.tiket.VerificationTicketFactory
import it.valeriovaudi.vauthenticator.document.DocumentRepository
import it.valeriovaudi.vauthenticator.mail.*
import it.valeriovaudi.vauthenticator.mfa.MfaMethodsEnrolmentAssociation
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplicationRepository
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