package com.vauthenticator.server.communication.email

typealias EMailContext = Map<String, Any>

enum class EMailType(val path: String) {
    WELCOME("templates/welcome.html"),
    EMAIL_VERIFICATION("templates/email-verify-challenge.html"),
    RESET_PASSWORD("templates/reset-password.html"),
    MFA("templates/mfa-challenge.html");
}

data class EMailMessage(val to: String, val from: String, val subject: String, val type: EMailType = EMailType.WELCOME, val context: EMailContext)
data class EMailTemplate(val emailType: EMailType, val body: String)
