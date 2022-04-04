package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.mail.JavaMailSenderService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender

@Configuration(proxyBeanMethods = false)
class MailServiceConfig {

    @Bean
    fun mailSenderService(javaMailSender: JavaMailSender) = JavaMailSenderService(javaMailSender)

}