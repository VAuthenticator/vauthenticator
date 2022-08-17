package it.valeriovaudi.vauthenticator.mail

typealias MailContext = Map<String, Any>

enum class MailType(val path: String) {
    SIGN_UP("templates/sign-up.html"), EMAIL_VERIFICATION(""), RESET_PASSWORD("");
}

data class MailMessage(val to: String, val from: String, val subject: String, val type: MailType = MailType.SIGN_UP, val context: MailContext)
