package it.valeriovaudi.vauthenticator.oauth2.clientapp

import it.valeriovaudi.vauthenticator.cache.CacheContentConverter
import it.valeriovaudi.vauthenticator.cache.CacheOperation
import java.util.*

class CachedClientApplicationRepository(
    private val cacheContentConverter: CacheContentConverter<ClientApplication>,
    private val cacheOperation: CacheOperation<String, String>,
    private val delegate: ClientApplicationRepository
) : ClientApplicationRepository by delegate {

    override fun findOne(clientAppId: ClientAppId): Optional<ClientApplication> {
        return cacheOperation.get(clientAppId.content)
            .flatMap { Optional.empty<ClientApplication>() }
            .or {
                val clientApp = delegate.findOne(clientAppId)
                clientApp.ifPresent {
                    val loadableContentIntoCache = cacheContentConverter.loadableContentIntoCacheFor(it)
                    cacheOperation.put(clientAppId.content, loadableContentIntoCache)
                }
                clientApp
            }
    }
}