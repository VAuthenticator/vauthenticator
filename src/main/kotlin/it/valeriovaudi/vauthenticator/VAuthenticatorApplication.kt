package it.valeriovaudi.vauthenticator

import it.valeriovaudi.vauthenticator.mail.NoReplyMailConfiguration
import it.valeriovaudi.vauthenticator.mfa.OtpConfigurationProperties
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute


@SpringBootApplication
@EnableConfigurationProperties(NoReplyMailConfiguration::class, OtpConfigurationProperties::class)
class VAuthenticatorApplication

fun main(args: Array<String>) {
    runApplication<VAuthenticatorApplication>(*args)
}

@ControllerAdvice
class BaseUiModelInjector(@Value("\${assetServer.baseUrl:http://localhost:3000}") private val assetServerBaseUrl: String) {

    @ModelAttribute("assetServerBaseUrl")
    fun assetServerBaseUrl() = assetServerBaseUrl

}