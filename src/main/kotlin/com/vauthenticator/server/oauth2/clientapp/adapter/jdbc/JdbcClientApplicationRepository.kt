package com.vauthenticator.server.oauth2.clientapp.adapter.jdbc

import com.fasterxml.jackson.databind.ObjectMapper
import com.vauthenticator.server.oauth2.clientapp.domain.*
import org.springframework.jdbc.core.JdbcTemplate
import java.sql.ResultSet
import java.util.*

private const val FINED_ALL_QUERY = """
    SELECT client_app_id,
    secret,
    scopes,
    with_pkce,
    authorized_grant_types,
    web_server_redirect_uri,
    access_token_validity,
    refresh_token_validity,
    additional_information,
    auto_approve,
    post_logout_redirect_uri,
logout_uri FROM CLIENT_APPLICATION
"""
private const val FINED_ONE_QUERY = """
    SELECT client_app_id,
    secret,
    scopes,
    with_pkce,
    authorized_grant_types,
    web_server_redirect_uri,
    access_token_validity,
    refresh_token_validity,
    additional_information,
    auto_approve,
    post_logout_redirect_uri,
logout_uri FROM CLIENT_APPLICATION WHERE client_app_id=?
"""
private const val SAVE_QUERY = """
    INSERT INTO CLIENT_APPLICATION (
    client_app_id,
    secret,
    scopes,
    with_pkce,
    authorized_grant_types,
    web_server_redirect_uri,
    access_token_validity,
    refresh_token_validity,
    additional_information,
    auto_approve,
    post_logout_redirect_uri,
    logout_uri)
     VALUES (?,?,?,?,?,?,?,?,?,?,?,?)  ON CONFLICT(client_app_id) DO UPDATE SET secret=?,
    scopes=?,
    with_pkce=?,
    authorized_grant_types=?,
    web_server_redirect_uri=?,
    access_token_validity=?,
    refresh_token_validity=?,
    additional_information=?,
    auto_approve=?,
    post_logout_redirect_uri=?,
logout_uri=?
"""
private const val DELETE_QUERY = """DELETE FROM CLIENT_APPLICATION WHERE client_app_id=?"""

class JdbcClientApplicationRepository(private val jdbcTemplate: JdbcTemplate, private val objectMapper: ObjectMapper) :
    ClientApplicationRepository {
    override fun findOne(clientAppId: ClientAppId): Optional<ClientApplication> {
        val queryResult = jdbcTemplate.query(FINED_ONE_QUERY, { rs, _ ->
            JdbcClientApplicationConverter.fromDbToDomain(rs, objectMapper)
        }, clientAppId.content)
        return Optional.ofNullable(queryResult.firstOrNull())
    }

    override fun findAll(): Iterable<ClientApplication> {
        return jdbcTemplate.query(FINED_ALL_QUERY) { rs, _ ->
            JdbcClientApplicationConverter.fromDbToDomain(rs, objectMapper)
        }
    }

    override fun save(clientApp: ClientApplication) {
        jdbcTemplate.update(
            SAVE_QUERY,
            clientApp.clientAppId.content,

            clientApp.secret.content,
            clientApp.scopes.content.joinToString { it.content },
            clientApp.withPkce.content,
            clientApp.authorizedGrantTypes.content.joinToString { it.name },
            clientApp.webServerRedirectUri.content,
            clientApp.accessTokenValidity.content,
            clientApp.refreshTokenValidity.content,
            objectMapper.writeValueAsString(clientApp.additionalInformation),
            clientApp.autoApprove.content,
            clientApp.postLogoutRedirectUri.content,
            clientApp.logoutUri.content,

            clientApp.secret.content,
            clientApp.scopes.content.joinToString { it.content },
            clientApp.withPkce.content,
            clientApp.authorizedGrantTypes.content.joinToString { it.name },
            clientApp.webServerRedirectUri.content,
            clientApp.accessTokenValidity.content,
            clientApp.refreshTokenValidity.content,
            objectMapper.writeValueAsString(clientApp.additionalInformation),
            clientApp.autoApprove.content,
            clientApp.postLogoutRedirectUri.content,
            clientApp.logoutUri.content,
        )
    }

    override fun delete(clientAppId: ClientAppId) {
        jdbcTemplate.update(DELETE_QUERY, clientAppId.content)
    }

}

object JdbcClientApplicationConverter {

    fun fromDbToDomain(rs: ResultSet, objectMapper : ObjectMapper) = ClientApplication(
        clientAppId = ClientAppId(rs.getString("client_app_id")),
        secret = Secret(rs.getString("secret")),
        scopes = Scopes(rs.getString("scopes").split(",").map { Scope(it.trim()) }.toSet()),
        withPkce = WithPkce(rs.getBoolean("with_pkce")),
        authorizedGrantTypes = AuthorizedGrantTypes(
            rs.getString("authorized_grant_types").split(",").map { AuthorizedGrantType.valueOf(it) }),
        webServerRedirectUri = CallbackUri(rs.getString("web_server_redirect_uri")),
        accessTokenValidity = TokenTimeToLive(rs.getLong("access_token_validity")),
        refreshTokenValidity = TokenTimeToLive(rs.getLong("refresh_token_validity")),
        additionalInformation = Optional.ofNullable(objectMapper.readValue(
            rs.getString("additional_information"),
            Map::class.java
        ) as Map<String, String>).orElse(emptyMap()),
        autoApprove = AutoApprove(rs.getBoolean("auto_approve")),
        postLogoutRedirectUri = PostLogoutRedirectUri(rs.getString("post_logout_redirect_uri")),
        logoutUri = LogoutUri(rs.getString("logout_uri"))
    )
}