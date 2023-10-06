package com.vauthenticator.server.config

import com.hubspot.jinjava.Jinjava
import com.vauthenticator.document.repository.DocumentRepository
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.account.welcome.SayWelcome
import com.vauthenticator.server.account.welcome.SendWelcomeMailUponSignUpEventConsumer
import com.vauthenticator.server.mail.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender

@Configuration(proxyBeanMethods = false)
class WelcomeConfig {

    @Bean
    fun sayWelcome(
        accountRepository: AccountRepository,
        welcomeMailSender: MailSenderService
    ) = SayWelcome(accountRepository, welcomeMailSender)

    @Bean
    fun welcomeMailSender(
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
                noReplyMailConfiguration.welcomeMailSubject,
                MailType.WELCOME
            )
        )

    @Bean
    fun sendWelcomeMailUponSignUpEventConsumer(sayWelcome: SayWelcome) =
        SendWelcomeMailUponSignUpEventConsumer(sayWelcome)
}