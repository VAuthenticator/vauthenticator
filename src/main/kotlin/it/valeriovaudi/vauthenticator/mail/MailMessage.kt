package it.valeriovaudi.vauthenticator.mail

typealias MailContext = Map<String, Any>

enum class MailType(val path: String) {
    WELCOME("templates/welcome.html"),
    EMAIL_VERIFICATION("templates/mail-verify-challenge.html"),
    SUCCESSFUL_EMAIL_VERIFICATION("templates/successful-mail-verification.html"),
    RESET_PASSWORD("templates/reset-password.html"),
    MFA("templates/mfa-challenge.html");
}

data class MailMessage(val to: String, val from: String, val subject: String, val type: MailType = MailType.WELCOME, val context: MailContext)
