package com.vauthenticator.server.config

import com.github.benmanes.caffeine.cache.Caffeine
import com.vauthenticator.document.repository.DocumentRepository
import com.vauthenticator.server.document.DocumentType
import com.vauthenticator.server.web.StaticController
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration


@Configuration(proxyBeanMethods = false)
class CacheConfig(private val documentRepository: DocumentRepository) {

    private val logger = LoggerFactory.getLogger(StaticController::class.java)

    @Bean
    @ConditionalOnProperty("embedded-asset-cdn.enabled", havingValue = "true", matchIfMissing = true)
    fun staticContentLocalCache(@Value("\${embedded-asset-cdn.cache.ttl:1m}") ttl: Duration): CaffeineCache {
        return CaffeineCache(
            "static-content-local-cache", Caffeine.newBuilder()
                .refreshAfterWrite(ttl)
                .build { assetName ->
                    logger.debug("loading $assetName ....")
                    documentRepository.loadDocument(DocumentType.STATIC.content, "content/asset/$assetName")
                }
        )
    }

}