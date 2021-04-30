package it.valeriovaudi.vauthenticator.oauth2.clientapp

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet
import java.util.*

interface ClientApplicationRepository {

    fun findOne(clientAppId: ClientAppId): Optional<ClientApplication>

    fun findLogoutUriByFederation(federation: Federation): Iterable<LogoutUri>

    fun findAll(): Iterable<ClientApplication>

    fun save(clientApp: ClientApplication)

    fun delete(clientAppId: ClientAppId)
}

class ClientApplicationNotFound(message: String) : RuntimeException(message)

@Transactional
class JdbcClientApplicationRepository(private val jdbcTemplate: JdbcTemplate) : ClientApplicationRepository {
    val INSERT_QUERY: String = """
    INSERT INTO oauth_client_details 
    (
        client_id,
        client_secret,
        resource_ids,
        scope,
        authorized_grant_types,
        web_server_redirect_uri,
        authorities,
        access_token_validity, 
        refresh_token_validity,
        additional_information, 
        autoapprove,
        post_logout_redirect_uris,
        logout_uris,
        federation
    )
     VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?) 
     ON CONFLICT (client_id) DO UPDATE SET 
        client_secret=?,
        resource_ids=?,
        scope=?,
        authorized_grant_types=?,
        web_server_redirect_uri=?,
        authorities=?,
        access_token_validity=?, 
        refresh_token_validity=?,
        additional_information=?, 
        autoapprove=?,
        post_logout_redirect_uris=?,
        logout_uris=?,
        federation=?
""".trimIndent()

    override fun findOne(clientAppId: ClientAppId): Optional<ClientApplication> {
        return Optional.ofNullable(
                jdbcTemplate.query("SELECT * FROM oauth_client_details WHERE client_id = ?",
                        arrayOf(clientAppId.content),
                        mapper).firstOrNull())
    }

    override fun findLogoutUriByFederation(federation: Federation) =
            jdbcTemplate.query("SELECT * FROM oauth_client_details WHERE federation = ?",
                    arrayOf(federation.name),
                    mapper)
                .map { it.logoutUri }

    override fun findAll(): Iterable<ClientApplication> =
            jdbcTemplate.query("SELECT * FROM oauth_client_details", mapper)

    override fun save(clientApp: ClientApplication) {
        clientApp.let {
            val parametes = listOf(
                    it.secret.content,
                    it.resourceIds.content.map { it.content }.reduce(joinWithComma()),
                    it.scopes.content.map { it.content }.reduce(joinWithComma()),
                    it.authorizedGrantTypes.content.map { it.name.toLowerCase() }.reduce(joinWithComma()),
                    it.webServerRedirectUri.content,
                    it.authorities.content.map { it.content }.reduce(joinWithComma()),
                    it.accessTokenValidity.content,
                    it.refreshTokenValidity.content,
                    null,
                    it.autoApprove.content,
                    it.postLogoutRedirectUri.content,
                    it.logoutUri.content,
                    it.federation.name)

            val list = listOf(
                    it.clientAppId.content
            ) + parametes + parametes

            jdbcTemplate.update(INSERT_QUERY, *list.toTypedArray())
        }
    }

    private fun joinWithComma() = { acc: String, s: String -> "$acc,$s" }

    override fun delete(clientAppId: ClientAppId) {
        jdbcTemplate.update("DELETE FROM oauth_client_details WHERE client_id=?", clientAppId.content)
    }

    private val mapper: (ResultSet, Int) -> ClientApplication =
            { rs: ResultSet, i: Int ->
                ClientApplication(
                        clientAppId = ClientAppId(rs.getString("client_id")),
                        secret = Secret(rs.getString("client_secret")),
                        authorizedGrantTypes = AuthorizedGrantTypes(listFor(rs, "authorized_grant_types")
                                .map { AuthorizedGrantType.valueOf(it.toUpperCase()) }),
                        scopes = Scopes(listFor(rs, "scope").map { Scope(it) }.toSet()),
                        autoApprove = AutoApprove(rs.getBoolean("autoapprove")),
                        accessTokenValidity = TokenTimeToLive(rs.getInt("access_token_validity")),
                        refreshTokenValidity = TokenTimeToLive(rs.getInt("refresh_token_validity")),
                        webServerRedirectUri = CallbackUri(rs.getString("web_server_redirect_uri")),
                        logoutUri = LogoutUri(rs.getString("logout_uris")),
                        postLogoutRedirectUri = PostLogoutRedirectUri(rs.getString("post_logout_redirect_uris")),
                        authorities = Authorities(listFor(rs, "authorities").map { Authority(it) }),
                        federation = Federation(rs.getString("federation")),
                        resourceIds = ResourceIds.from(ResourceId(rs.getString("resource_ids"))),
                        additionalInformation = emptyMap()
                )
            }

    private fun listFor(rs: ResultSet, fieldName: String): List<String> {
        return Optional.ofNullable(rs.getString(fieldName))
                .map { it.split(",") }
                .orElse(listOf())
    }

}