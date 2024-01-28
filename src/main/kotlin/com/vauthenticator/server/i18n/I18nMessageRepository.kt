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
            val resource = messageResourceFor(scope, userLang)
            properties.load(resource.inputStream)
            return I18nMessages(properties.toMap() as Map<String, String>)
        } catch (e: Exception) {
            logger.error(e.message, e)
            return I18nMessages.empty()
        }
    }

    private fun messageResourceFor(
        scope: I18nScope,
        userLang: String
    ): Resource {
        var resource = resourceLoader.getResource("${ResourceLoader.CLASSPATH_URL_PREFIX}${scope.path}_${userLang}.properties")
        if (!resource.exists()) {
            resource = resourceLoader.getResource("${ResourceLoader.CLASSPATH_URL_PREFIX}${scope.path}.properties")
        }
        return resource
    }

}