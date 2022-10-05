package it.valeriovaudi.vauthenticator.mail

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("no-reply.mail")
data class NoReplyMailConfiguration(val from: String = "", val welcomeMailSubject: String = "", val verificationMailSubject: String = "", val resetPasswordMailSubject: String = "")