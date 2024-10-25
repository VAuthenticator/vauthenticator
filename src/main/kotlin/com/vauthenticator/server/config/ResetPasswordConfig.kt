package com.vauthenticator.server.config

import com.hubspot.jinjava.Jinjava
import com.vauthenticator.document.repository.DocumentRepository
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.communication.NoReplyEMailConfiguration
import com.vauthenticator.server.communication.adapter.JinJavaTemplateResolver
import com.vauthenticator.server.communication.adapter.javamail.JavaEMailSenderService
import com.vauthenticator.server.communication.domain.EMailSenderService
import com.vauthenticator.server.communication.domain.EMailType
import com.vauthenticator.server.communication.domain.SimpleEMailMessageFactory
import com.vauthenticator.server.events.VAuthenticatorEventsDispatcher
import com.vauthenticator.server.oauth2.clientapp.domain.ClientApplicationRepository
import com.vauthenticator.server.password.domain.PasswordPolicy
import com.vauthenticator.server.password.domain.VAuthenticatorPasswordEncoder
import com.vauthenticator.server.password.domain.resetpassword.ResetAccountPassword
import com.vauthenticator.server.password.domain.resetpassword.SendResetPasswordMailChallenge
import com.vauthenticator.server.ticket.domain.TicketCreator
import com.vauthenticator.server.ticket.domain.TicketRepository
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
        ticketCreator: TicketCreator,
        resetPasswordMailSender: EMailSenderService,
        @Value("\${vauthenticator.host}") frontChannelBaseUrl: String
    ) =
        SendResetPasswordMailChallenge(
            accountRepository,
            ticketCreator,
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
        noReplyEMailConfiguration: NoReplyEMailConfiguration
    ) =
        JavaEMailSenderService(
            documentRepository,
            javaMailSender,
            JinJavaTemplateResolver(Jinjava()),
            SimpleEMailMessageFactory(
                noReplyEMailConfiguration.from,
                noReplyEMailConfiguration.resetPasswordEMailSubject,
                EMailType.RESET_PASSWORD
            )
        )

}