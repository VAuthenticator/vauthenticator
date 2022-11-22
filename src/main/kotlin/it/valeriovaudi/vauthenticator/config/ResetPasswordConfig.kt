package it.valeriovaudi.vauthenticator.config

import com.hubspot.jinjava.Jinjava
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.account.resetpassword.ResetAccountPassword
import it.valeriovaudi.vauthenticator.account.resetpassword.SendResetPasswordMailChallenge
import it.valeriovaudi.vauthenticator.account.tiket.TicketRepository
import it.valeriovaudi.vauthenticator.account.tiket.VerificationTicketFactory
import it.valeriovaudi.vauthenticator.document.DocumentRepository
import it.valeriovaudi.vauthenticator.mail.*
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplicationRepository
import it.valeriovaudi.vauthenticator.password.VAuthenticatorPasswordEncoder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender

@Configuration(proxyBeanMethods = false)
class ResetPasswordConfig {

    @Bean
    fun sendResetPasswordMailChallenge(
        accountRepository: AccountRepository,
        clientApplicationRepository: ClientApplicationRepository,
        verificationTicketFactory: VerificationTicketFactory,
        resetPasswordMailSender: MailSenderService,
        @Value("\${vauthenticator.host}") frontChannelBaseUrl: String
    ) =
        SendResetPasswordMailChallenge(
            clientApplicationRepository,
            accountRepository,
            verificationTicketFactory,
            resetPasswordMailSender,
            frontChannelBaseUrl
        )

    @Bean
    fun resetPasswordChallengeSent(
        accountRepository: AccountRepository,
        vAuthenticatorPasswordEncoder: VAuthenticatorPasswordEncoder,
        ticketRepository: TicketRepository
    ) =
        ResetAccountPassword(accountRepository, vAuthenticatorPasswordEncoder, ticketRepository)

    @Bean
    fun resetPasswordMailSender(
        javaMailSender: JavaMailSender,
        documentRepository: DocumentRepository,
        noReplyMailConfiguration: NoReplyMailConfiguration
    ) =
        JavaMailSenderService(
            documentRepository,
            javaMailSender,
            JinjavaMailTemplateResolver(Jinjava()),
            SimpleMailMessageFactory(
                noReplyMailConfiguration.from,
                noReplyMailConfiguration.resetPasswordMailSubject,
                MailType.RESET_PASSWORD
            )
        )

}