package com.vauthenticator.server.config

import com.hubspot.jinjava.Jinjava
import com.vauthenticator.document.repository.DocumentRepository
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.account.ticket.TicketRepository
import com.vauthenticator.server.account.ticket.VerificationTicketFactory
import com.vauthenticator.server.events.VAuthenticatorEventsDispatcher
import com.vauthenticator.server.mail.*
import com.vauthenticator.server.oauth2.clientapp.ClientApplicationRepository
import com.vauthenticator.server.password.PasswordPolicy
import com.vauthenticator.server.password.VAuthenticatorPasswordEncoder
import com.vauthenticator.server.password.resetpassword.ResetAccountPassword
import com.vauthenticator.server.password.resetpassword.SendResetPasswordMailChallenge
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
            accountRepository,
            verificationTicketFactory,
            resetPasswordMailSender,
            frontChannelBaseUrl
        )

    @Bean
    fun resetPasswordChallengeSent(
        eventsDispatcher: VAuthenticatorEventsDispatcher,
        accountRepository: AccountRepository,
        vAuthenticatorPasswordEncoder: VAuthenticatorPasswordEncoder,
        passwordPolicy: PasswordPolicy,
        ticketRepository: TicketRepository
    ) =
        ResetAccountPassword(eventsDispatcher, accountRepository, vAuthenticatorPasswordEncoder,passwordPolicy, ticketRepository)

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