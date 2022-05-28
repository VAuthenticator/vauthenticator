package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.mail.JavaMailSenderService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessagePreparator
import java.io.InputStream
import javax.mail.internet.MimeMessage

@Configuration(proxyBeanMethods = false)
class MailServiceConfig {

    @Bean("mailSenderService")
    @ConditionalOnProperty(value = ["sign-up.mail.enabled"], havingValue = "false", matchIfMissing = false)
    fun mailSenderService() = JavaMailSenderService(object : JavaMailSender {
        override fun send(mimeMessage: MimeMessage) {
            TODO("Not yet implemented")
        }

        override fun send(vararg mimeMessages: MimeMessage?) {
            TODO("Not yet implemented")
        }

        override fun send(mimeMessagePreparator: MimeMessagePreparator) {
            TODO("Not yet implemented")
        }

        override fun send(vararg mimeMessagePreparators: MimeMessagePreparator?) {
            TODO("Not yet implemented")
        }

        override fun send(simpleMessage: SimpleMailMessage) {
            TODO("Not yet implemented")
        }

        override fun send(vararg simpleMessages: SimpleMailMessage?) {
            TODO("Not yet implemented")
        }

        override fun createMimeMessage(): MimeMessage {
            TODO("Not yet implemented")
        }

        override fun createMimeMessage(contentStream: InputStream): MimeMessage {
            TODO("Not yet implemented")
        }
    })

    @Bean("mailSenderService")
    @ConditionalOnProperty(value = ["sign-up.mail.enabled"], havingValue = "true", matchIfMissing = false)
    fun mailSenderService(javaMailSender: JavaMailSender) = JavaMailSenderService(javaMailSender)

}