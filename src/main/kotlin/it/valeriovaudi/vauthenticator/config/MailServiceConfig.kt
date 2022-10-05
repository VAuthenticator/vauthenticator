package it.valeriovaudi.vauthenticator.config

import com.hubspot.jinjava.Jinjava
import it.valeriovaudi.vauthenticator.account.signup.SignUpConfirmationMailConfiguration
import it.valeriovaudi.vauthenticator.document.DocumentRepository
import it.valeriovaudi.vauthenticator.mail.JavaMailSenderService
import it.valeriovaudi.vauthenticator.mail.JinjavaMailTemplateResolver
import it.valeriovaudi.vauthenticator.mail.MailType
import it.valeriovaudi.vauthenticator.mail.SimpleMailMessageFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender

@Configuration(proxyBeanMethods = false)
class MailServiceConfig {

    @Bean
    fun welcomeMailSender(javaMailSender: JavaMailSender, documentRepository: DocumentRepository, signUpConfirmationMailConfiguration: SignUpConfirmationMailConfiguration) =
            JavaMailSenderService(documentRepository,
                    javaMailSender,
                    JinjavaMailTemplateResolver(Jinjava()),
                    SimpleMailMessageFactory(signUpConfirmationMailConfiguration.from, signUpConfirmationMailConfiguration.subject, MailType.WELCOME)
            )

}