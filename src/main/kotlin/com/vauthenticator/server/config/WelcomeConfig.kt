package com.vauthenticator.server.config

import com.hubspot.jinjava.Jinjava
import com.vauthenticator.document.repository.DocumentRepository
import com.vauthenticator.server.account.domain.AccountRepository
import com.vauthenticator.server.account.domain.welcome.SayWelcome
import com.vauthenticator.server.account.domain.welcome.SendWelcomeMailUponSignUpEventConsumer
import com.vauthenticator.server.communication.NoReplyEMailConfiguration
import com.vauthenticator.server.communication.adapter.JinJavaTemplateResolver
import com.vauthenticator.server.communication.adapter.javamail.JavaEMailSenderService
import com.vauthenticator.server.communication.domain.EMailSenderService
import com.vauthenticator.server.communication.domain.EMailType
import com.vauthenticator.server.communication.domain.SimpleEMailMessageFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender

@Configuration(proxyBeanMethods = false)
class WelcomeConfig {

    @Bean
    fun sayWelcome(
        accountRepository: AccountRepository,
        welcomeMailSender: EMailSenderService
    ) = SayWelcome(accountRepository, welcomeMailSender)

    @Bean
    fun welcomeMailSender(
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
                noReplyEMailConfiguration.welcomeEMailSubject,
                EMailType.WELCOME
            )
        )

    @Bean
    fun sendWelcomeMailUponSignUpEventConsumer(sayWelcome: SayWelcome) =
        SendWelcomeMailUponSignUpEventConsumer(sayWelcome)
}