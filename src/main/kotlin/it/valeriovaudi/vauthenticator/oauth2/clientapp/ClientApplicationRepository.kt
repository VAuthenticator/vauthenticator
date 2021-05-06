package it.valeriovaudi.vauthenticator.oauth2.clientapp

import java.util.*

interface ClientApplicationRepository {

    fun findOne(clientAppId: ClientAppId): Optional<ClientApplication>

    fun findLogoutUriByFederation(federation: Federation): Iterable<LogoutUri>

    fun findAll(): Iterable<ClientApplication>

    fun save(clientApp: ClientApplication)

    fun delete(clientAppId: ClientAppId)
}

class ClientApplicationNotFound(message: String) : RuntimeException(message)