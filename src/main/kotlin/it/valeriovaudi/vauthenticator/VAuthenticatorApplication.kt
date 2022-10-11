package it.valeriovaudi.vauthenticator

import it.valeriovaudi.vauthenticator.mail.NoReplyMailConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(NoReplyMailConfiguration::class)
class VAuthenticatorApplication

fun main(args: Array<String>) {
    runApplication<VAuthenticatorApplication>(*args)
}
