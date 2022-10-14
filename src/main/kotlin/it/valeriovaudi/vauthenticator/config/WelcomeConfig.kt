package it.valeriovaudi.vauthenticator.config

import com.hubspot.jinjava.Jinjava
import it.valeriovaudi.vauthenticator.document.DocumentRepository
import it.valeriovaudi.vauthenticator.mail.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender

@Configuration(proxyBeanMethods = false)
class WelcomeConfig {

    @Bean
    fun welcomeMailSender(javaMailSender: JavaMailSender, documentRepository: DocumentRepository, noReplyMailConfiguration: NoReplyMailConfiguration) =
            JavaMailSenderService(documentRepository,
                    javaMailSender,
                    JinjavaMailTemplateResolver(Jinjava()),
                    SimpleMailMessageFactory(noReplyMailConfiguration.from, noReplyMailConfiguration.welcomeMailSubject, MailType.WELCOME)
            )
}