package com.vauthenticator.server.oauth2.clientapp.adapter.jdbc

import com.vauthenticator.server.oauth2.clientapp.domain.ClientAppId
import com.vauthenticator.server.oauth2.clientapp.domain.ClientApplication
import com.vauthenticator.server.oauth2.clientapp.domain.ClientApplicationRepository
import org.springframework.jdbc.core.JdbcTemplate
import java.util.*

private const val SAVE_QUERY = ""

class JdbcClientApplicationRepository(private val jdbcTemplate: JdbcTemplate) : ClientApplicationRepository {
    override fun findOne(clientAppId: ClientAppId): Optional<ClientApplication> {
        TODO()
    }

    override fun findAll(): Iterable<ClientApplication> {
        TODO()
    }

    override fun save(clientApp: ClientApplication) {
        //todo to be completed
        jdbcTemplate.update(SAVE_QUERY)
    }

    override fun delete(clientAppId: ClientAppId) {
        TODO()
    }

}