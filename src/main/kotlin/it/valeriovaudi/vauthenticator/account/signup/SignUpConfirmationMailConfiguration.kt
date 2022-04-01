package it.valeriovaudi.vauthenticator.account.signup

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("sign-up.mail")
data class SignUpConfirmationMailConfiguration(val from: String, val subject: String, val bodyTemplate: String)