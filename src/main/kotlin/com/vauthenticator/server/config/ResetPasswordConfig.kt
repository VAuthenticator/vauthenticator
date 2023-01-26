package com.vauthenticator.server.config

import com.hubspot.jinjava.Jinjava
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.account.resetpassword.ResetAccountPassword
import com.vauthenticator.server.account.resetpassword.SendResetPasswordMailChallenge
import com.vauthenticator.server.account.tiket.TicketRepository
import com.vauthenticator.server.account.tiket.VerificationTicketFactory
import com.vauthenticator.server.document.DocumentRepository
import com.vauthenticator.server.mail.*
import com.vauthenticator.server.oauth2.clientapp.ClientApplicationRepository
import com.vauthenticator.server.password.PasswordPolicy
import com.vauthenticator.server.password.VAuthenticatorPasswordEncoder
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
        passwordPolicy: PasswordPolicy,
        ticketRepository: TicketRepository
    ) =
        ResetAccountPassword(accountRepository, vAuthenticatorPasswordEncoder,passwordPolicy, ticketRepository)

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