package com.vauthenticator.server.web

import com.vauthenticator.server.document.Document
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
@ConditionalOnProperty("asset-server.on-s3.enabled", havingValue = "true", matchIfMissing = true)
class StaticController(private val staticContentLocalCache: CaffeineCache) {

    @GetMapping("/static/content/asset/{assetName}")
    fun assetContent(@PathVariable assetName: String): ByteArray {
        return staticContentLocalCache.get(assetName, Document::class.java)!!.content
    }
}