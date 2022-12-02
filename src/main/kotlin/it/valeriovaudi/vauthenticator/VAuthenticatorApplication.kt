package it.valeriovaudi.vauthenticator

import it.valeriovaudi.vauthenticator.mail.NoReplyMailConfiguration
import it.valeriovaudi.vauthenticator.mfa.OtpConfigurationProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(NoReplyMailConfiguration::class, OtpConfigurationProperties::class)
class VAuthenticatorApplication

fun main(args: Array<String>) {
    runApplication<VAuthenticatorApplication>(*args)
}
