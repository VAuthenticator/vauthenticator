package com.vauthenticator.server.i18n

typealias I18nMessageKey = String
typealias I18nMessageValue = String

@JvmInline
value class I18nMessages(val content: Map<I18nMessageKey, I18nMessageValue>){
    companion object {
        fun empty() = I18nMessages(emptyMap())
    }
}

enum class I18nScope (val prefix : String){
    LOGIN_PAGE("login_page"),
    SIGN_UP_PAGE("sign_up_page"),
    SUCCESSFUL_SIGN_UP_PAGE("successful_sign_up_page"),
    MAIL_VERIFY_PAGE("mail_verify_page"),
    RESET_PASSWORD_PAGE("reset_password_page"),
    CHANGE_PASSWORD_PAGE("change_password_page"),
    MFA_PAGE("mfa_page"),
}