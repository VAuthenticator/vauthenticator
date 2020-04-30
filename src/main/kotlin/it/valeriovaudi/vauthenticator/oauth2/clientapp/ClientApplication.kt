package it.valeriovaudi.vauthenticator.oauth2.clientapp

import org.springframework.jdbc.core.JdbcTemplate
import java.sql.ResultSet
import java.util.*

/*client_id
resource_ids
client_secret
scope
authorized_grant_types
web_server_redirect_uri
authorities
access_token_validity
refresh_token_validity
additional_information
autoapprove
post_logout_redirect_uris
logout_uris*/
data class ClientApplication(val clientAppId: ClientAppId,
                             val secret: Secret,
                             val scope: Scopes,
                             val authorizedGrantTypes: AuthorizedGrantTypes,
                             val webServerRedirectUri: CallbackUri,
                             val authorities: Authorities,
                             val accessTokenValidity: TokenTimeToLive,
                             val refreshTokenValidity: TokenTimeToLive,
                             val additionalInformation: Map<String, Objects>,
                             val autoApprove: AutoApprove,
                             val postLogoutRedirectUri: PostLogoutRedirectUri,
                             val logoutUri: LogoutUri,
                             val federation: Federation,
                             val resourceIds: ResourceIds
)

data class AutoApprove(val content: Boolean) {
    companion object {
        val approve = AutoApprove(true)
        val disapprove = AutoApprove(false)
    }
}

data class AuthorizedGrantTypes(val content: List<AuthorizedGrantType>) {
    companion object {
        fun from(vararg authorizedGrantType: AuthorizedGrantType) = AuthorizedGrantTypes(listOf(*authorizedGrantType))
    }
}

enum class AuthorizedGrantType { CLIENT_CREDENTIALS, PASSWORD, AUTHORIZATION_CODE, IMPLICIT, REFRESH_TOKEN }

data class ResourceIds(val content: List<ResourceId>) {
    companion object {
        fun from(vararg resourceId: ResourceId) = ResourceIds(listOf(*resourceId))
    }
}

data class ResourceId(val content: String)

data class ClientAppId(val content: String)
data class Secret(val content: String)
data class CallbackUri(val content: String)
data class PostLogoutRedirectUri(val content: String)
data class LogoutUri(val content: String)

data class Scopes(val content: List<Scope>) {
    companion object {
        fun from(vararg scope: Scope) = Scopes(listOf(*scope))
    }
}

data class Scope(val content: String) {
    companion object {
        val OPEN_ID = Scope("openid")
        val PROFILE = Scope("profile")
        val EMAIL = Scope("email")
    }
}

data class Authorities(val content: List<Authority>)
data class Authority(val content: String)
data class TokenTimeToLive(val content: Int)
data class Federation(val name: String)

interface ClientApplicationRepository {

    fun findOne(clientAppId: ClientAppId): Optional<ClientApplication>

    fun findByFederation(federation: Federation): Iterable<ClientApplication>

    fun findAll(): Iterable<ClientApplication>

    fun save(clientApp: ClientApplication)

    fun delete(clientAppId: ClientAppId)
}

class JdbcClientApplicationRepository(private val jdbcTemplate: JdbcTemplate) : ClientApplicationRepository {

    override fun findOne(clientAppId: ClientAppId): Optional<ClientApplication> {
        return Optional.ofNullable(
                jdbcTemplate.query("SELECT * FROM oauth_client_details WHERE client_id = ?",
                        arrayOf(clientAppId.content),
                        mapper).firstOrNull())
    }

    override fun findByFederation(federation: Federation) =
            jdbcTemplate.query("SELECT * FROM oauth_client_details WHERE federation = ?",
                    arrayOf(federation.name),
                    mapper)

    override fun findAll(): Iterable<ClientApplication> =
            jdbcTemplate.query("SELECT * FROM oauth_client_details", mapper)

    override fun save(clientApp: ClientApplication) {
        TODO("Not yet implemented")
    }

    override fun delete(clientAppId: ClientAppId) {
        TODO("Not yet implemented")
    }

    private val mapper: (ResultSet, Int) -> ClientApplication =
            { rs: ResultSet, i: Int ->
                ClientApplication(
                        clientAppId = ClientAppId(rs.getString("client_id")),
                        secret = Secret(rs.getString("client_secret")),
                        authorizedGrantTypes = AuthorizedGrantTypes(listFor(rs, "authorized_grant_types")
                                .map { AuthorizedGrantType.valueOf(it.toUpperCase()) }),
                        scope = Scopes(listFor(rs, "scope").map { Scope(it) }),
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