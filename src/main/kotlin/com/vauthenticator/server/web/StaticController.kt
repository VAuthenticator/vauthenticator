package com.vauthenticator.server.web

import com.vauthenticator.document.repository.Document
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
@ConditionalOnProperty("embedded-asset-cdn.enabled", havingValue = "true", matchIfMissing = true)
class StaticController(private val staticAssetDocumentLocalCache: CaffeineCache) {

    @GetMapping("/static/content/asset/{assetName}")
    fun assetContent(@PathVariable assetName: String): ByteArray {
        return staticAssetDocumentLocalCache.get(assetName, Document::class.java)!!.content
    }
}