package com.vauthenticator.server.i18n

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.ui.Model
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.servlet.LocaleResolver


class I18nMessageInjector(
    private val localeResolver: LocaleResolver,
    private val objectMapper: ObjectMapper,
    private val i18nMessageRepository: I18nMessageRepository
) {
    fun setMessagedFor(
        i18nScope: I18nScope,
        model: Model
    ) {
        val servletRequest = currentHttpServletRequest()
        val userLang = localeResolver.resolveLocale(servletRequest).toLanguageTag()
        val i18nMessages = i18nMessageRepository.getMessagedFor(i18nScope, userLang)
        model.addAttribute("i18nMessages", objectMapper.writeValueAsString(i18nMessages.messages))
        model.addAttribute("errors", objectMapper.writeValueAsString(i18nMessages.error))
    }

    private fun currentHttpServletRequest() =
        (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request

}