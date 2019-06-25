package it.valeriovaudi.vauthenticator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.integration.annotation.IntegrationComponentScan
import org.springframework.integration.config.EnableIntegration


@EnableCaching
@EnableIntegration
@SpringBootApplication
@IntegrationComponentScan
class VauthenticatorApplication

fun main(args: Array<String>) {
    runApplication<VauthenticatorApplication>(*args)
}
