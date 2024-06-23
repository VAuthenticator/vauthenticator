package com.vauthenticator.server.email

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("no-reply.email")
data class NoReplyEMailConfiguration(
    val from: String = "",
    val welcomeMailSubject: String = "",
    val verificationMailSubject: String = "",
    val resetPasswordMailSubject: String = "",
    val mfaMailSubject: String = ""
)