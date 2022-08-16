package it.valeriovaudi.vauthenticator.config

import com.hubspot.jinjava.Jinjava
import it.valeriovaudi.vauthenticator.document.DocumentRepository
import it.valeriovaudi.vauthenticator.mail.JavaMailSenderService
import it.valeriovaudi.vauthenticator.mail.JinjavaMailTemplateResolver
import it.valeriovaudi.vauthenticator.mail.MailSenderService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender

@Configuration(proxyBeanMethods = false)
class MailServiceConfig {

    @Bean("mailSenderService")
    fun mailSenderService(javaMailSender: JavaMailSender,
                          documentRepository: DocumentRepository): MailSenderService =
            JavaMailSenderService(documentRepository, javaMailSender, JinjavaMailTemplateResolver(Jinjava()))

}