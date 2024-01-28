package com.vauthenticator.server.i18n

import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import java.util.*

class I18nMessageRepository(private val resourceLoader: ResourceLoader) {

    private val logger = LoggerFactory.getLogger(I18nMessageRepository::class.java)
    fun getMessagedFor(scope: I18nScope, userLang: String): I18nMessages {
        try {
            val properties = Properties()
            val resource = messageResourceFor(userLang)
            properties.load(resource.inputStream)
            val content = (properties.toMap() as Map<String, String>)
                .filter { it.key.startsWith(scope.prefix) }
                .mapKeys { it.key.removePrefix("${scope.prefix}.") }
            return I18nMessages(content)
        } catch (e: Exception) {
            logger.error(e.message, e)
            return I18nMessages.empty()
        }
    }

    private fun messageResourceFor(
        userLang: String
    ): Resource {
        var resource = resourceLoader.getResource("${ResourceLoader.CLASSPATH_URL_PREFIX}/i18n/messages_${userLang}.properties")
        if (!resource.exists()) {
            resource = resourceLoader.getResource("${ResourceLoader.CLASSPATH_URL_PREFIX}/i18n/messages.properties")
        }
        return resource
    }

}