package com.vauthenticator.server.oauth2.clientapp.adapter.inmemory

import com.vauthenticator.server.oauth2.clientapp.domain.AllowedOrigin
import com.vauthenticator.server.oauth2.clientapp.domain.AllowedOriginRepository
import com.vauthenticator.server.oauth2.clientapp.domain.AllowedOrigins
import com.vauthenticator.server.oauth2.clientapp.domain.ClientAppId
import com.vauthenticator.server.oauth2.clientapp.domain.ClientApplicationRepository

class InMemoryAllowedOriginRepository(
    private val clientApplicationRepository: ClientApplicationRepository
) : AllowedOriginRepository {
    private val storage = mutableMapOf<ClientAppId, AllowedOrigins>()

    init {
        clientApplicationRepository.findAll()
            .forEach {
                storage.put(it.clientAppId, it.allowedOrigins)
            }
    }

    override fun getAllAvailableAllowedOrigins(): Set<AllowedOrigin> {
        return storage.flatMap { it.value.content.toList() }.toSet()
    }

    override fun setAllowedOriginsFor(clientAppId: ClientAppId, allowedOrigins: AllowedOrigins) {
        storage.put(clientAppId, allowedOrigins)
    }

}