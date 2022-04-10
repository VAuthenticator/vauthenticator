package it.valeriovaudi.vauthenticator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class VAuthenticatorManagementUIApplication

fun main(args: Array<String>) {
    runApplication<VAuthenticatorManagementUIApplication>(*args)
}
