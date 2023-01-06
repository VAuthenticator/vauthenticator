package com.vauthenticator.config

import com.hubspot.jinjava.Jinjava
import com.vauthenticator.account.repository.AccountRepository
import com.vauthenticator.account.resetpassword.ResetAccountPassword
import com.vauthenticator.account.resetpassword.SendResetPasswordMailChallenge
import com.vauthenticator.account.tiket.TicketRepository
import com.vauthenticator.account.tiket.VerificationTicketFactory
import com.vauthenticator.document.DocumentRepository
import com.vauthenticator.mail.*
import com.vauthenticator.oauth2.clientapp.ClientApplicationRepository
import com.vauthenticator.password.VAuthenticatorPasswordEncoder
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