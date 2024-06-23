package com.vauthenticator.server.email

typealias MailContext = Map<String, Any>

enum class MailType(val path: String) {
    WELCOME("templates/welcome.html"),
    EMAIL_VERIFICATION("templates/email-verify-challenge.html"),
    RESET_PASSWORD("templates/reset-password.html"),
    MFA("templates/mfa-challenge.html");
}

data class MailMessage(val to: String, val from: String, val subject: String, val type: MailType = MailType.WELCOME, val context: MailContext)
data class MailTemplate(val mailType: MailType, val body: String)
