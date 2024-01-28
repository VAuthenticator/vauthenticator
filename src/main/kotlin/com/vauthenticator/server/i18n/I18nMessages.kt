package com.vauthenticator.server.i18n

typealias I18nMessageKey = String
typealias I18nMessageValue = String

@JvmInline
value class I18nMessages(val content: Map<I18nMessageKey, I18nMessageValue>){
    companion object {
        fun empty() = I18nMessages(emptyMap())
    }
}

enum class I18nScope (val path : String){
    LOGIN_PAGE("/i18n/login_page")
}