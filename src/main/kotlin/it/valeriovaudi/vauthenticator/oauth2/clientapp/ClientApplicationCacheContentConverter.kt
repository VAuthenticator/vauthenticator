package it.valeriovaudi.vauthenticator.oauth2.clientapp

import it.valeriovaudi.vauthenticator.cache.CacheContentConverter

class ClientApplicationCacheContentConverter : CacheContentConverter<ClientApplication> {
    override fun getObjectFromCacheContentFor(cacheContent: String): ClientApplication {
        TODO("Not yet implemented")
    }

    override fun loadableContentIntoCacheFor(source: ClientApplication): String {
        TODO("Not yet implemented")
    }
}